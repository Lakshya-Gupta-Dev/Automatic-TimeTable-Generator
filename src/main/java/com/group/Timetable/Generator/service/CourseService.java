package com.group.Timetable.Generator.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.group.Timetable.Generator.Repository.CoursesRepository;
import com.group.Timetable.Generator.Repository.DepartmentRepository;
import com.group.Timetable.Generator.Repository.UserRepository;
import com.group.Timetable.Generator.dto.CourseDTO;
import com.group.Timetable.Generator.entities.Courses;
import com.group.Timetable.Generator.entities.Department;
import com.group.Timetable.Generator.entities.User;
import com.group.Timetable.Generator.mapper.CourseMapper;

@Service
public class CourseService {

    private final CoursesRepository courseRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    public CourseService(CoursesRepository courseRepository,
                         DepartmentRepository departmentRepository,
                         UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    // ================= ADD =================
    public CourseDTO saveCourse(CourseDTO dto) {
        User user = getAuthenticatedUser();

        // Find department ONLY of logged user
        Department dept = departmentRepository
                .findByDeptNameAndUser(dto.getDepartmentName(), user)
                .orElseThrow(() -> new RuntimeException("Department not found for this user"));

        // Duplicate check
        if (courseRepository.findByCourseNameIgnoreCaseAndUser(dto.getCourseName(), user).isPresent()) {
            throw new RuntimeException("Course already exists for this user");
        }

        Courses course = Courses.builder()
                .courseName(dto.getCourseName())
                .semester(dto.getSemester())
                .department(dept)
                .user(user)
                .build();

        return CourseMapper.toDTO(courseRepository.save(course));
    }


    // ================= GET ALL FOR USER =================
    public List<CourseDTO> getCoursesForAuthenticatedUser() {
        User user = getAuthenticatedUser();
        return courseRepository.findByUser(user)
                .stream()
                .map(CourseMapper::toDTO)
                .toList();
    }


    // ================= GET BY ID =================
    public CourseDTO getCourseById(Long id) {
        User user = getAuthenticatedUser();

        Courses course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }

        return CourseMapper.toDTO(course);
    }


    // ================= UPDATE =================
    public CourseDTO updateCourse(Long id, CourseDTO dto) {
        User user = getAuthenticatedUser();

        Courses course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized update attempt");
        }

        // Check duplicate (except same record)
        courseRepository.findByCourseNameIgnoreCaseAndUser(dto.getCourseName(), user)
                .filter(c -> !c.getCourseId().equals(id))
                .ifPresent(c -> {
                    throw new RuntimeException("Another course with same name exists for this user");
                });

        Department dept = departmentRepository
                .findByDeptNameAndUser(dto.getDepartmentName(), user)
                .orElseThrow(() -> new RuntimeException("Department not found for this user"));

        course.setCourseName(dto.getCourseName());
        course.setSemester(dto.getSemester());
        course.setDepartment(dept);

        return CourseMapper.toDTO(courseRepository.save(course));
    }


    // ================= DELETE =================
    public void deleteCourse(Long id) {
        User user = getAuthenticatedUser();

        Courses course = courseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!course.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized delete attempt");
        }

        courseRepository.delete(course);
    }


    // ================= SEARCH BY NAME (User-specific) =================
    public List<CourseDTO> searchByName(String name) {
        User user = getAuthenticatedUser();

        return courseRepository.findByCourseNameContainingIgnoreCaseAndUser(name, user)
                .stream()
                .map(CourseMapper::toDTO)
                .toList();
    }


    // ================= FIND BY DEPARTMENT NAME =================
    public List<CourseDTO> findByDepartmentName(String deptName) {
        User user = getAuthenticatedUser();

        Department dept = departmentRepository
                .findByDeptNameAndUser(deptName, user)
                .orElseThrow(() -> new RuntimeException("Department not found for this user"));

        return courseRepository.findByDepartmentAndUser(dept, user)
                .stream()
                .map(CourseMapper::toDTO)
                .toList();
    }
}


