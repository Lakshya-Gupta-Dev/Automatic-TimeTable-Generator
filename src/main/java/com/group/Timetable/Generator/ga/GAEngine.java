package com.group.Timetable.Generator.ga;

import com.group.Timetable.Generator.dto.GAInputDTO;
import java.util.*;
import java.util.stream.Collectors;

public class GAEngine {

    private final GAInputDTO input;
    private final int populationSize;
    private final int generations;
    private final double mutationRate;
    private final Random rnd = new Random();

    private final int ASSIGN_TRIES = 80;

    public GAEngine(GAInputDTO input, int populationSize, int generations, double mutationRate) {
        this.input = input;
        this.populationSize = Math.max(10, populationSize);
        this.generations = Math.max(1, generations);
        this.mutationRate = Math.max(0.0, Math.min(1.0, mutationRate));
    }

    public GAChromosome run() {
        List<GAChromosome> population = new ArrayList<>();
        for (int i = 0; i < populationSize; i++) population.add(randomChromosome());

        FitnessCalculator fitnessCalc = new FitnessCalculator(input);
        population.forEach(c -> c.setFitness(fitnessCalc.calculate(c)));
        Collections.sort(population);

        for (int gen = 0; gen < generations; gen++) {
            List<GAChromosome> nextGen = new ArrayList<>();
            nextGen.add(population.get(0).copy());
            if (population.size() > 1) nextGen.add(population.get(1).copy());

            while (nextGen.size() < populationSize) {
                GAChromosome p1 = tournamentSelect(population);
                GAChromosome p2 = tournamentSelect(population);
                GAChromosome child = crossover(p1, p2);
                mutate(child);
                normalizeChromosome(child);
                child.setFitness(fitnessCalc.calculate(child));
                nextGen.add(child);
            }

            population = nextGen;
            Collections.sort(population);
        }

        return population.get(0);
    }

    private Map<Long, Map<Long, List<Long>>> buildSectionSubjectTeacherMap() {
        Map<Long, Map<Long, List<Long>>> out = new HashMap<>();
        for (GAInputDTO.GATeacherSubjectDTO m : input.getTeacherSubjectMappings()) {
            if (m.getSectionId() == null || m.getSubjectId() == null || m.getTeacherId() == null) continue;
            out.computeIfAbsent(m.getSectionId(), k -> new HashMap<>())
               .computeIfAbsent(m.getSubjectId(), k -> new ArrayList<>())
               .add(m.getTeacherId());
        }
        return out;
    }

    private Map<Long, GAInputDTO.GASubjectDTO> subjectsById() {
        return input.getSubjects().stream()
                .filter(s -> s.getSubjectId() != null)
                .collect(Collectors.toMap(GAInputDTO.GASubjectDTO::getSubjectId, s -> s, (a,b)->a));
    }

    private Map<Long, GAInputDTO.GARoomDTO> roomsById() {
        return input.getRooms().stream()
                .collect(Collectors.toMap(GAInputDTO.GARoomDTO::getRoomId, r -> r, (a,b)->a));
    }

    private Map<String, List<Long>> roomIdsByType() {
        Map<String, List<Long>> out = new HashMap<>();
        for (GAInputDTO.GARoomDTO r : input.getRooms()) {
            String t = r.getRoomType() == null ? "THEORY" : r.getRoomType().toUpperCase(Locale.ROOT);
            out.computeIfAbsent(t, k -> new ArrayList<>()).add(r.getRoomId());
        }
        return out;
    }

    private GAChromosome randomChromosome() {
        List<GAGene> genes = new ArrayList<>();
        Map<Long, GAInputDTO.GASubjectDTO> subjectMeta = subjectsById();
        Map<Long, GAInputDTO.GARoomDTO> roomMeta = roomsById();
        Map<String, List<Long>> roomsByType = roomIdsByType();
        List<Long> slotIds = input.getTimeSlots().stream().map(GAInputDTO.GATimeSlotDTO::getSlotId).toList();

        Map<Long, Map<Long, List<Long>>> sectionMap = buildSectionSubjectTeacherMap();

        Set<String> usedRoomSlot = new HashSet<>();
        Set<String> usedTeacherSlot = new HashSet<>();
        Set<String> usedSectionSlot = new HashSet<>();

        for (GAInputDTO.GASectionDTO sec : input.getSections()) {

            Map<Long, List<Long>> subjectsForSection = sectionMap.getOrDefault(sec.getSectionId(), Collections.emptyMap());

            // Branch-specific filter
            subjectsForSection = subjectsForSection.entrySet().stream()
                .filter(e -> {
                    GAInputDTO.GASubjectDTO s = subjectMeta.get(e.getKey());
                    return s != null && (s.getBranch().equalsIgnoreCase("ALL") || s.getBranch().equalsIgnoreCase(sec.getBranch()));
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            for (Map.Entry<Long, List<Long>> entry : subjectsForSection.entrySet()) {

                Long subjId = entry.getKey();
                GAInputDTO.GASubjectDTO subMeta = subjectMeta.get(subjId);
                if (subMeta == null) continue;

                int repeats = Math.max(1, subMeta.getSubCredit());
                boolean isLab = subMeta.getSubType().toUpperCase().contains("LAB");

                List<Long> eligibleRooms = isLab ? roomsByType.getOrDefault("LAB", Collections.emptyList()) : roomsByType.getOrDefault("THEORY", Collections.emptyList());
                if (eligibleRooms.isEmpty()) eligibleRooms = roomMeta.keySet().stream().toList();

                List<Long> teacherList = entry.getValue();
                if (teacherList.isEmpty() && !input.getTeachers().isEmpty()) {
                    teacherList = input.getTeachers().stream().map(GAInputDTO.GATeacherDTO::getTeacherId).toList();
                }

                for (int r = 0; r < repeats; r++) {

                    Long chosenTeacher = teacherList.get(rnd.nextInt(teacherList.size()));
                    Long chosenSlot = null;
                    Long chosenRoom = null;

                    List<Long> shuffledSlots = new ArrayList<>(slotIds);
                    Collections.shuffle(shuffledSlots, rnd);

                    List<Long> shuffledRooms = new ArrayList<>(eligibleRooms);
                    Collections.shuffle(shuffledRooms, rnd);

                    boolean assigned = false;

                    outer:
                    for (Long slotId : shuffledSlots) {
                        for (Long roomId : shuffledRooms) {
                            String roomKey = slotId + "_" + roomId;
                            String teacherKey = slotId + "_" + chosenTeacher;
                            String sectionKey = slotId + "_" + sec.getSectionId();

                            if (usedRoomSlot.contains(roomKey) || usedTeacherSlot.contains(teacherKey) || usedSectionSlot.contains(sectionKey))
                                continue;

                            chosenSlot = slotId;
                            chosenRoom = roomId;

                            usedRoomSlot.add(roomKey);
                            usedTeacherSlot.add(teacherKey);
                            usedSectionSlot.add(sectionKey);

                            assigned = true;
                            break outer;
                        }
                    }

                    if (!assigned) {
                        chosenSlot = shuffledSlots.get(0);
                        chosenRoom = shuffledRooms.get(0);
                    }

                    genes.add(new GAGene(sec.getSectionId(), subjId, chosenTeacher, chosenRoom, chosenSlot));
                }
            }
        }

        GAChromosome chrom = new GAChromosome(genes);
        normalizeChromosome(chrom);
        return chrom;
    }

    private GAChromosome tournamentSelect(List<GAChromosome> population) {
        int tsize = Math.max(2, populationSize / 10);
        GAChromosome best = null;
        for (int i = 0; i < tsize; i++) {
            GAChromosome c = population.get(rnd.nextInt(population.size()));
            if (best == null || c.getFitness() > best.getFitness()) best = c;
        }
        return best.copy();
    }

    private GAChromosome crossover(GAChromosome p1, GAChromosome p2) {
        List<GAGene> g1 = p1.copy().getGenes();
        List<GAGene> g2 = p2.copy().getGenes();
        int size = Math.min(g1.size(), g2.size());
        if (size < 2) return p1.copy();
        int point = rnd.nextInt(size - 1) + 1;
        List<GAGene> child = new ArrayList<>();
        for (int i = 0; i < size; i++)
            child.add(i < point ? g1.get(i) : g2.get(i));
        GAChromosome c = new GAChromosome(child);
        normalizeChromosome(c);
        return c;
    }

    private void mutate(GAChromosome chrom) {
        List<GAGene> genes = chrom.getGenes();
        Map<Long, GAInputDTO.GASubjectDTO> subjectMeta = subjectsById();
        Map<String, List<Long>> roomsByType = roomIdsByType();
        List<Long> slots = slotsById().keySet().stream().toList();

        for (GAGene g : genes) {
            if (rnd.nextDouble() < mutationRate) {
                GAInputDTO.GASubjectDTO s = subjectMeta.get(g.getSubjectId());
                if (s == null) continue;

                boolean isLab = s.getSubType().toUpperCase().contains("LAB");
                int choice = rnd.nextInt(2);
                if (choice == 0) {
                    List<Long> eligibleRooms = isLab ? roomsByType.getOrDefault("LAB", Collections.emptyList()) : roomsByType.getOrDefault("THEORY", Collections.emptyList());
                    if (!eligibleRooms.isEmpty())
                        g.setRoomId(eligibleRooms.get(rnd.nextInt(eligibleRooms.size())));
                } else {
                    if (!slots.isEmpty())
                        g.setTimeSlotId(slots.get(rnd.nextInt(slots.size())));
                }
            }
        }
        normalizeChromosome(chrom);
    }

    private void normalizeChromosome(GAChromosome chrom) {

        Map<Long, GAInputDTO.GASubjectDTO> subjects = subjectsById();
        Map<String, List<Long>> roomsByType = roomIdsByType();
        List<Long> slotIds = slotsById().keySet().stream().toList();
        Map<Long, GAInputDTO.GARoomDTO> roomMeta = roomsById();

        Map<String, List<GAGene>> grouped = new HashMap<>();
        for (GAGene g : chrom.getGenes()) {
            String key = g.getSectionId() + "_" + g.getSubjectId();
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(g);
        }

        List<GAGene> result = new ArrayList<>(chrom.getGenes());

        for (GAInputDTO.GASectionDTO sec : input.getSections()) {

            List<GAInputDTO.GASubjectDTO> validSubjects = input.getSubjects().stream()
                    .filter(s -> Objects.equals(s.getCourseId(), sec.getCourseId()))
                    .filter(s -> s.getBranch().equalsIgnoreCase("ALL") || s.getBranch().equalsIgnoreCase(sec.getBranch()))
                    .toList();

            for (GAInputDTO.GASubjectDTO sub : validSubjects) {

                int required = Math.max(1, sub.getSubCredit());
                String key = sec.getSectionId() + "_" + sub.getSubjectId();
                List<GAGene> existing = grouped.getOrDefault(key, new ArrayList<>());

                if (existing.size() > required) {
                    int extra = existing.size() - required;
                    for (int i = 0; i < extra; i++) {
                        GAGene rem = existing.remove(rnd.nextInt(existing.size()));
                        result.remove(rem);
                    }
                }

                if (existing.size() < required) {

                    int need = required - existing.size();
                    List<Long> teacherChoices = input.getTeacherSubjectMappings().stream()
                            .filter(m -> Objects.equals(m.getSectionId(), sec.getSectionId()) &&
                                         Objects.equals(m.getSubjectId(), sub.getSubjectId()))
                            .map(GAInputDTO.GATeacherSubjectDTO::getTeacherId)
                            .distinct()
                            .toList();

                    Long teacherId = teacherChoices.isEmpty()
                            ? input.getTeachers().get(rnd.nextInt(input.getTeachers().size())).getTeacherId()
                            : teacherChoices.get(rnd.nextInt(teacherChoices.size()));

                    boolean isLab = sub.getSubType().toUpperCase().contains("LAB");
                    List<Long> eligibleRooms = isLab ? roomsByType.getOrDefault("LAB", Collections.emptyList()) : roomsByType.getOrDefault("THEORY", Collections.emptyList());
                    if (eligibleRooms.isEmpty()) eligibleRooms = roomMeta.keySet().stream().toList();

                    Set<String> usedRoomSlot = new HashSet<>();
                    Set<String> usedTeacherSlot = new HashSet<>();
                    Set<String> usedSectionSlot = new HashSet<>();

                    for (GAGene g : result) {
                        usedRoomSlot.add(g.getTimeSlotId() + "_" + g.getRoomId());
                        usedTeacherSlot.add(g.getTimeSlotId() + "_" + g.getTeacherId());
                        usedSectionSlot.add(g.getTimeSlotId() + "_" + g.getSectionId());
                    }

                    for (int k = 0; k < need; k++) {
                        Long chosenSlot = null;
                        Long chosenRoom = null;

                        List<Long> shuffledSlots = new ArrayList<>(slotIds);
                        Collections.shuffle(shuffledSlots, rnd);

                        List<Long> shuffledRooms = new ArrayList<>(eligibleRooms);
                        Collections.shuffle(shuffledRooms, rnd);

                        boolean assigned = false;
                        outer:
                        for (Long slotId : shuffledSlots) {
                            for (Long roomId : shuffledRooms) {
                                String rKey = slotId + "_" + roomId;
                                String tKey = slotId + "_" + teacherId;
                                String sKey = slotId + "_" + sec.getSectionId();

                                if (usedRoomSlot.contains(rKey) || usedTeacherSlot.contains(tKey) || usedSectionSlot.contains(sKey))
                                    continue;

                                chosenSlot = slotId;
                                chosenRoom = roomId;
                                usedRoomSlot.add(rKey);
                                usedTeacherSlot.add(tKey);
                                usedSectionSlot.add(sKey);
                                assigned = true;
                                break outer;
                            }
                        }

                        if (!assigned) {
                            chosenSlot = shuffledSlots.get(0);
                            chosenRoom = shuffledRooms.get(0);
                        }

                        GAGene newGene = new GAGene(sec.getSectionId(), sub.getSubjectId(), teacherId, chosenRoom, chosenSlot);
                        result.add(newGene);
                    }
                }
            }
        }

        chrom.setGenes(result);
    }
    
    private Map<Long, GAInputDTO.GATimeSlotDTO> slotsById() {
        return input.getTimeSlots().stream()
                .collect(Collectors.toMap(GAInputDTO.GATimeSlotDTO::getSlotId, ts -> ts, (a,b) -> a));
    }

}
