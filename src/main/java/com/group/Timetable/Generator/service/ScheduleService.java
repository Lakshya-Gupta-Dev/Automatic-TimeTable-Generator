package com.group.Timetable.Generator.service;

import com.group.Timetable.Generator.Repository.*;
import com.group.Timetable.Generator.entities.*;
import com.group.Timetable.Generator.ga.GAChromosome;
import com.group.Timetable.Generator.ga.GAGene;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepo;
    private final SectionRepository sectionRepo;
    private final SubjectRepository subjectRepo;
    private final TeacherRepository teacherRepo;
    private final RoomRepository roomRepo;
    private final TimeSlotRepository timeSlotRepo;
    private final UserRepository userRepo;
    private final TeacherSubjectMappingRepository mappingRepo;

    public ScheduleService(ScheduleRepository scheduleRepo,
                           SectionRepository sectionRepo,
                           SubjectRepository subjectRepo,
                           TeacherRepository teacherRepo,
                           RoomRepository roomRepo,
                           TimeSlotRepository timeSlotRepo,
                           UserRepository userRepo,
                           TeacherSubjectMappingRepository mappingRepo) {

        this.scheduleRepo = scheduleRepo;
        this.sectionRepo = sectionRepo;
        this.subjectRepo = subjectRepo;
        this.teacherRepo = teacherRepo;
        this.roomRepo = roomRepo;
        this.timeSlotRepo = timeSlotRepo;
        this.userRepo = userRepo;
        this.mappingRepo = mappingRepo;
    }

    private Long getAuthenticatedUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
        return user.getId();
    }

    public List<Schedule> getUserSchedule() {
        return scheduleRepo.findByUserId(getAuthenticatedUserId());
    }

    // ---------------------------------------------------
    // 🔥 FINAL GA-BASED SAVE FUNCTION (with mapping check)
    // ---------------------------------------------------
    public void saveGeneratedSchedule(GAChromosome bestChromosome) {

        Long userId = getAuthenticatedUserId();
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Remove old timetable
        scheduleRepo.deleteByUserId(userId);

        // Unique generation ID
        String generationId = "GEN-" + System.currentTimeMillis();

        for (GAGene g : bestChromosome.getGenes()) {

            if (g.getSectionId() == null || g.getSubjectId() == null ||
                g.getTeacherId() == null || g.getRoomId() == null ||
                g.getTimeSlotId() == null)
                continue;

            Section section = sectionRepo.findById(g.getSectionId())
                    .orElseThrow(() -> new RuntimeException("Section not found"));

            Subjects subject = subjectRepo.findById(g.getSubjectId())
                    .orElseThrow(() -> new RuntimeException("Subject not found"));

            Teacher teacher = teacherRepo.findById(g.getTeacherId())
                    .orElseThrow(() -> new RuntimeException("Teacher not found"));

            // -------------------------------------------------
            // 🔥 MOST IMPORTANT: VALIDATE TEACHER-SUBJECT-SECTION mapping
            // -------------------------------------------------
            boolean validMapping = mappingRepo
                    .findByTeacherAndSubjectAndSectionAndUser(teacher, subject, section, user)
                    .isPresent();

            if (!validMapping) {
                // If mapping invalid: skip this gene
                continue;
            }

            Schedule s = Schedule.builder()
                    .user(user)
                    .generationId(generationId)
                    .section(section)
                    .subject(subject)
                    .teacher(teacher)
                    .room(roomRepo.findById(g.getRoomId())
                            .orElseThrow(() -> new RuntimeException("Room not found")))
                    .timeSlot(timeSlotRepo.findById(g.getTimeSlotId())
                            .orElseThrow(() -> new RuntimeException("TimeSlot not found")))
                    .build();

            scheduleRepo.save(s);
        }
    }

    // ---------------------------------------------------
    // 🔥 MANUAL TIMETABLE SAVE (used for manual entries)
    // ---------------------------------------------------
    public void saveGenerated(List<Map<String, Object>> rows) {
        Long userId = getAuthenticatedUserId();
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        scheduleRepo.deleteByUserId(userId);

        for (Map<String, Object> row : rows) {

            Long sectionId = row.get("sectionId") != null ? Long.valueOf(row.get("sectionId").toString()) : null;
            Long subjectId = row.get("subjectId") != null ? Long.valueOf(row.get("subjectId").toString()) : null;
            Long teacherId = row.get("teacherId") != null ? Long.valueOf(row.get("teacherId").toString()) : null;
            Long roomId = row.get("roomId") != null ? Long.valueOf(row.get("roomId").toString()) : null;
            Long timeSlotId = row.get("timeSlotId") != null ? Long.valueOf(row.get("timeSlotId").toString()) : null;

            if (sectionId == null || subjectId == null || teacherId == null ||
                roomId == null || timeSlotId == null)
                continue;

            Schedule s = Schedule.builder()
                    .user(user)
                    .section(sectionRepo.findById(sectionId)
                            .orElseThrow(() -> new RuntimeException("Section not found")))
                    .subject(subjectRepo.findById(subjectId)
                            .orElseThrow(() -> new RuntimeException("Subject not found")))
                    .teacher(teacherRepo.findById(teacherId)
                            .orElseThrow(() -> new RuntimeException("Teacher not found")))
                    .room(roomRepo.findById(roomId)
                            .orElseThrow(() -> new RuntimeException("Room not found")))
                    .timeSlot(timeSlotRepo.findById(timeSlotId)
                            .orElseThrow(() -> new RuntimeException("TimeSlot not found")))
                    .build();

            scheduleRepo.save(s);
        }
        
        
    }
    
    public List<Schedule> searchSchedules(String q) {
        Long userId = getAuthenticatedUserId();

        return scheduleRepo
                .findByUserIdAndTeacher_TeacherNameContainingIgnoreCaseOrUserIdAndSubject_SubNameContainingIgnoreCaseOrUserIdAndSection_SectionNameContainingIgnoreCaseOrUserIdAndRoom_RoomNameContainingIgnoreCaseOrUserIdAndTimeSlot_DayContainingIgnoreCase(
                        userId, q,
                        userId, q,
                        userId, q,
                        userId, q,
                        userId, q
                );
    }

}

