package com.group.Timetable.Generator.ga;

import com.group.Timetable.Generator.dto.GAInputDTO;
import java.util.*;
import java.util.stream.Collectors;

public class FitnessCalculator {

    private final GAInputDTO input;

    private final double ROOM_CLASH_PENALTY = 50.0;
    private final double TEACHER_CLASH_PENALTY = 60.0;
    private final double INVALID_MAPPING_PENALTY = 300.0;
    private final double INVALID_ROOMTYPE_PENALTY = 150.0;
    private final double MISSING_LECTURE_PENALTY = 80.0;
    private final double EXTRA_LECTURE_PENALTY = 30.0;
    private final double SUBJECT_PLACED_REWARD = 5.0;

    public FitnessCalculator(GAInputDTO input) {
        this.input = input;
    }

    public double calculate(GAChromosome chromosome) {
        double score = 0.0;

        Map<Long, Map<Long, List<Long>>> sectionMap = new HashMap<>();
        for (GAInputDTO.GATeacherSubjectDTO m : input.getTeacherSubjectMappings()) {
            if (m.getSectionId() == null || m.getSubjectId() == null || m.getTeacherId() == null) continue;
            sectionMap.computeIfAbsent(m.getSectionId(), k -> new HashMap<>())
                      .computeIfAbsent(m.getSubjectId(), k -> new ArrayList<>())
                      .add(m.getTeacherId());
        }

        Map<Long, GAInputDTO.GASubjectDTO> subjectMeta = input.getSubjects().stream()
                .filter(s -> s.getSubjectId() != null)
                .collect(Collectors.toMap(GAInputDTO.GASubjectDTO::getSubjectId, s -> s, (a,b)->a));

        Map<Long, GAInputDTO.GARoomDTO> roomMeta = input.getRooms().stream()
                .filter(r -> r.getRoomId() != null)
                .collect(Collectors.toMap(GAInputDTO.GARoomDTO::getRoomId, r -> r, (a,b)->a));

        Map<String, Integer> roomOcc = new HashMap<>();
        Map<String, Integer> teacherOcc = new HashMap<>();
        Map<String, Integer> sectionOcc = new HashMap<>();
        Map<String, Integer> subjCountPerSection = new HashMap<>();

        for (GAGene g : chromosome.getGenes()) {
            Long subjId = g.getSubjectId();
            Long teacherId = g.getTeacherId();
            Long roomId = g.getRoomId();
            Long slotId = g.getTimeSlotId();
            Long sectionId = g.getSectionId();

            if (subjId == null || teacherId == null) {
                score -= INVALID_MAPPING_PENALTY;
                continue;
            }

            
            GAInputDTO.GASectionDTO secDto = input.getSections().stream()
                    .filter(sec -> sec.getSectionId().equals(sectionId))
                    .findFirst().orElse(null);

            GAInputDTO.GASubjectDTO subj = subjectMeta.get(subjId);
            if (secDto != null && subj != null) {
                if (!(subj.getBranch().equalsIgnoreCase("ALL") || subj.getBranch().equalsIgnoreCase(secDto.getBranch()))) {
                    score -= INVALID_MAPPING_PENALTY;
                    continue;
                }
            }

            boolean mappingValid = false;
            Map<Long, List<Long>> subjMap = sectionMap.getOrDefault(sectionId, Collections.emptyMap());
            List<Long> teachersFor = subjMap.getOrDefault(subjId, Collections.emptyList());
            if (!teachersFor.isEmpty() && teachersFor.contains(teacherId)) mappingValid = true;

            if (!mappingValid) score -= INVALID_MAPPING_PENALTY;

            if (roomId != null && subj != null) {
                boolean subjIsLab = (subj.getSubType() != null && subj.getSubType().toUpperCase().contains("LAB")) ||
                        (subj.getSubName() != null && subj.getSubName().toLowerCase().contains("lab"));
                GAInputDTO.GARoomDTO rmeta = roomMeta.get(roomId);
                if (rmeta == null) score -= INVALID_ROOMTYPE_PENALTY;
                else {
                    boolean roomIsLab = rmeta.getRoomType() != null && rmeta.getRoomType().toUpperCase().contains("LAB");
                    if (subjIsLab && !roomIsLab) score -= INVALID_ROOMTYPE_PENALTY;
                    if (!subjIsLab && roomIsLab) score -= INVALID_ROOMTYPE_PENALTY;
                }
            }

            if (slotId != null && roomId != null) roomOcc.put(slotId + "_" + roomId, roomOcc.getOrDefault(slotId + "_" + roomId, 0) + 1);
            if (slotId != null && teacherId != null) teacherOcc.put(slotId + "_" + teacherId, teacherOcc.getOrDefault(slotId + "_" + teacherId, 0) + 1);
            if (slotId != null && sectionId != null) sectionOcc.put(slotId + "_" + sectionId, sectionOcc.getOrDefault(slotId + "_" + sectionId, 0) + 1);

            String key = sectionId + "_" + subjId;
            subjCountPerSection.put(key, subjCountPerSection.getOrDefault(key, 0) + 1);

            score += SUBJECT_PLACED_REWARD;
        }

        for (Map.Entry<String, Integer> e : roomOcc.entrySet()) if (e.getValue() > 1) score -= ROOM_CLASH_PENALTY * (e.getValue() - 1);
        for (Map.Entry<String, Integer> e : teacherOcc.entrySet()) if (e.getValue() > 1) score -= TEACHER_CLASH_PENALTY * (e.getValue() - 1);
        for (Map.Entry<String, Integer> e : sectionOcc.entrySet()) if (e.getValue() > 1) score -= ROOM_CLASH_PENALTY * (e.getValue() - 1);

        Map<String, Integer> required = new HashMap<>();
        for (GAInputDTO.GASectionDTO sec : input.getSections()) {
            for (GAInputDTO.GATeacherSubjectDTO m : input.getTeacherSubjectMappings()) {
                if (!Objects.equals(m.getSectionId(), sec.getSectionId())) continue;
                GAInputDTO.GASubjectDTO sMeta = subjectMeta.get(m.getSubjectId());
                if (sMeta == null) continue;
                required.put(sec.getSectionId() + "_" + sMeta.getSubjectId(), Math.max(1, sMeta.getSubCredit()));
            }
        }

        for (Map.Entry<String, Integer> e : required.entrySet()) {
            String key = e.getKey();
            int req = e.getValue();
            int got = subjCountPerSection.getOrDefault(key, 0);
            if (got == req) score += req * 5.0;
            else if (got < req) score -= (req - got) * MISSING_LECTURE_PENALTY;
            else score -= (got - req) * EXTRA_LECTURE_PENALTY;
        }

        return score;
    }
}
