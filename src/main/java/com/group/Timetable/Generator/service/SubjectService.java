package com.group.Timetable.Generator.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.group.Timetable.Generator.Repository.CoursesRepository;
import com.group.Timetable.Generator.Repository.SubjectRepository;
import com.group.Timetable.Generator.Repository.UserRepository;
import com.group.Timetable.Generator.dto.SubjectDTO;
import com.group.Timetable.Generator.entities.Courses;
import com.group.Timetable.Generator.entities.Subjects;
import com.group.Timetable.Generator.entities.User;
import com.group.Timetable.Generator.mapper.SubjectMapper;



@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final CoursesRepository courseRepository;
    private final UserRepository userRepository;

    public SubjectService(SubjectRepository subjectRepository,
                          CoursesRepository courseRepository,
                          UserRepository userRepository) {
        this.subjectRepository = subjectRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    // ================= CREATE =================
    public SubjectDTO saveSubject(SubjectDTO dto) {
        User user = getAuthenticatedUser();

        Courses course = courseRepository
                .findByCourseNameIgnoreCaseAndUser(dto.getCourseName(), user)
                .orElseThrow(() -> new RuntimeException("Course not found for this user"));

        // Duplicate check
        if (subjectRepository.findBySubNameIgnoreCaseAndCoursesAndUser(dto.getSubName(), course, user).isPresent()) {
            throw new RuntimeException("Subject already exists in this course for this user");
        }

//        Subjects subject = Subjects.builder()
//                .subName(dto.getSubName())
//                .subCredit(dto.getSubCredit())
//                .subType(dto.getSubType())
//                .courses(course)
//                .user(user)
//                .build();

        Subjects subject = Subjects.builder()
                .subName(dto.getSubName())
                .semester(dto.getSemester())   // 🔥 important
                .subCredit(dto.getSubCredit())
                .subType(dto.getSubType())
                .branch(dto.getBranch())
                .courses(course)
                .user(user)
                .build();



        return SubjectMapper.toDTO(subjectRepository.save(subject));
    }

    // ================= GET ALL =================
    public List<SubjectDTO> getAllSubjectsForUser() {
        User user = getAuthenticatedUser();
        return subjectRepository.findByUser(user)
                .stream()
                .map(SubjectMapper::toDTO)
                .toList();
    }

    // ================= GET BY ID =================
    public SubjectDTO getSubjectById(Long id) {
        User user = getAuthenticatedUser();

        Subjects subject = subjectRepository.findById(id)
                .filter(s -> s.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Subject not found for this user"));

        return SubjectMapper.toDTO(subject);
    }

    // ================= UPDATE =================
    public SubjectDTO updateSubject(Long id, SubjectDTO dto) {
        User user = getAuthenticatedUser();

        Subjects subject = subjectRepository.findById(id)
                .filter(s -> s.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Subject not found for this user"));

        Courses course = courseRepository
                .findByCourseNameIgnoreCaseAndUser(dto.getCourseName(), user)
                .orElseThrow(() -> new RuntimeException("Course not found for this user"));

        // Duplicate check ignoring same subject record
        subjectRepository.findBySubNameIgnoreCaseAndCoursesAndUser(dto.getSubName(), course, user)
                .filter(s -> !s.getSubId().equals(id))
                .ifPresent(s -> { throw new RuntimeException("Another subject with same name already exists"); });

//        subject.setSubName(dto.getSubName());
//        subject.setSubType(dto.getSubType());
//        subject.setSubCredit(dto.getSubCredit());
//        subject.setCourses(course);
        
        
        subject.setSemester(dto.getSemester()); // Add this
        subject.setSubName(dto.getSubName());
        subject.setSubType(dto.getSubType());
        subject.setSubCredit(dto.getSubCredit());
        subject.setBranch(dto.getBranch());   // NEW

        subject.setCourses(course);



        return SubjectMapper.toDTO(subjectRepository.save(subject));
    }

    // ================= DELETE =================
    public void deleteSubject(Long id) {
        User user = getAuthenticatedUser();

        Subjects subject = subjectRepository.findById(id)
                .filter(s -> s.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Subject not found for this user"));

        subjectRepository.delete(subject);
    }

    // ================= SEARCH =================
    public List<SubjectDTO> searchByName(String name) {
        User user = getAuthenticatedUser();

        return subjectRepository.findBySubNameContainingIgnoreCaseAndUser(name, user)
                .stream()
                .map(SubjectMapper::toDTO)
                .toList();
    }

    // ================= FIND BY COURSE =================
    public List<SubjectDTO> findByCourseName(String courseName) {
        User user = getAuthenticatedUser();

        Courses course = courseRepository
                .findByCourseNameIgnoreCaseAndUser(courseName, user)
                .orElseThrow(() -> new RuntimeException("Course not found for this user"));

        return subjectRepository.findByCoursesAndUser(course, user)
                .stream()
                .map(SubjectMapper::toDTO)
                .toList();
    }
}
