package com.group.Timetable.Generator.service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.group.Timetable.Generator.Repository.*;
import com.group.Timetable.Generator.dto.GAInputDTO;
import com.group.Timetable.Generator.entities.*;

@Service
public class GAInputService {

    private final SectionRepository sectionRepo;
    private final SubjectRepository subjectRepo;
    private final TeacherRepository teacherRepo;
    private final TeacherSubjectMappingRepository mappingRepo;
    private final TimeSlotRepository timeSlotRepo;
    private final RoomRepository roomRepo;
    private final CoursesRepository courseRepo;
    private final UserRepository userRepo;

    private final DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm");

    public GAInputService(SectionRepository sectionRepo,
                          SubjectRepository subjectRepo,
                          TeacherRepository teacherRepo,
                          TeacherSubjectMappingRepository mappingRepo,
                          TimeSlotRepository timeSlotRepo,
                          RoomRepository roomRepo,
                          CoursesRepository courseRepo,
                          UserRepository userRepo) {
        this.sectionRepo = sectionRepo;
        this.subjectRepo = subjectRepo;
        this.teacherRepo = teacherRepo;
        this.mappingRepo = mappingRepo;
        this.timeSlotRepo = timeSlotRepo;
        this.roomRepo = roomRepo;
        this.courseRepo = courseRepo;
        this.userRepo = userRepo;
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    /**
     * Build GAInputDTO by collecting all required user-specific data.
     */
    public GAInputDTO buildInput() {

        User user = getAuthenticatedUser();

        // 1) SECTIONS
        List<GAInputDTO.GASectionDTO> sections = sectionRepo.findByUser(user)
                .stream()
                .map(s -> GAInputDTO.GASectionDTO.builder()
                        .sectionId(s.getSectionId())
                        .sectionName(s.getSectionName())
                        .semester(s.getSemester())
                        .courseId(s.getCourse() != null ? s.getCourse().getCourseId() : null)
                        .courseName(s.getCourse() != null ? s.getCourse().getCourseName() : null)
                        .branch(s.getBranch()) // NEW
                        .build())
                .collect(Collectors.toList());

  
        List<GAInputDTO.GASubjectDTO> subjects = subjectRepo.findByUser(user)
                .stream()
                .map(sub -> GAInputDTO.GASubjectDTO.builder()
                        .subjectId(sub.getSubId())
                        .subName(sub.getSubName())
                        .subCredit(sub.getSubCredit())
                        .subType(sub.getSubType())
                        .courseId(sub.getCourses() != null ? sub.getCourses().getCourseId() : null)
                        .semester(sub.getSemester())
                        .branch(sub.getBranch() == null ? "ALL" : sub.getBranch().toUpperCase())
                        .build())
                .collect(Collectors.toList());

     
        subjects = subjects.stream()
                .filter(sub ->
                        sub.getBranch().equalsIgnoreCase("ALL") ||
                        sections.stream().anyMatch(sec ->
                                sec.getBranch().equalsIgnoreCase(sub.getBranch())
                        )
                )
                .collect(Collectors.toList());




        // 3) TEACHERS
        List<GAInputDTO.GATeacherDTO> teachers = teacherRepo.findByUser(user)
                .stream()
                .map(t -> GAInputDTO.GATeacherDTO.builder()
                        .teacherId(t.getTeacherId())
                        .teacherName(t.getTeacherName())
                        .teacherEmail(t.getTeacherEmail())
                        .departmentId(t.getDepartment() != null ? t.getDepartment().getDeptId() : null)
                        .departmentName(t.getDepartment() != null ? t.getDepartment().getDeptName() : null)
                        .build())
                .collect(Collectors.toList());

        // 4) TEACHER–SUBJECT–SECTION MAPPING (section-specific)
        List<GAInputDTO.GATeacherSubjectDTO> mappings = mappingRepo.findByUser(user)
                .stream()
                .map(m -> GAInputDTO.GATeacherSubjectDTO.builder()
                        .mappingId(m.getId())
                        .teacherId(m.getTeacher() != null ? m.getTeacher().getTeacherId() : null)
                        .subjectId(m.getSubject() != null ? m.getSubject().getSubId() : null)
                        .sectionId(m.getSection() != null ? m.getSection().getSectionId() : null) // NEW
                        .build())
                .collect(Collectors.toList());

        // 5) ROOMS
        List<GAInputDTO.GARoomDTO> rooms = roomRepo.findByUser(user)
                .stream()
                .map(r -> GAInputDTO.GARoomDTO.builder()
                        .roomId(r.getRoomId())
                        .roomName(r.getRoomName())
                        .roomType(r.getRoomType())
                        .build())
                .collect(Collectors.toList());

        // 6) TIME SLOTS
        List<GAInputDTO.GATimeSlotDTO> slots = timeSlotRepo.findByUserId(user.getId())
                .stream()
                .map(ts -> GAInputDTO.GATimeSlotDTO.builder()
                        .slotId(ts.getSlotId())
                        .day(ts.getDay())
                        .startTime(ts.getStartTime().format(timeFmt))
                        .endTime(ts.getEndTime().format(timeFmt))
                        .slotNumber(ts.getSlotNumber())
                        .build())
                .collect(Collectors.toList());

        // VALIDATIONS
        if (sections.isEmpty())
            throw new RuntimeException("No sections found. Add sections before generating timetable.");

        if (subjects.isEmpty())
            throw new RuntimeException("No subjects found. Add subjects before generating timetable.");

        if (teachers.isEmpty())
            throw new RuntimeException("No teachers found. Add teachers before generating timetable.");

        if (mappings.isEmpty())
            throw new RuntimeException("No teacher-subject-section mappings found. Add mappings before generating timetable.");

        if (rooms.isEmpty())
            throw new RuntimeException("No rooms found. Add rooms before generating timetable.");

        if (slots.isEmpty())
            throw new RuntimeException("No timeslots found. Add timeslots before generating timetable.");

        return GAInputDTO.builder()
                .sections(sections)
                .subjects(subjects)
                .teachers(teachers)
                .teacherSubjectMappings(mappings)
                .rooms(rooms)
                .timeSlots(slots)
                .build();
    }
}

