
package com.group.Timetable.Generator.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.group.Timetable.Generator.Repository.DepartmentRepository;
import com.group.Timetable.Generator.Repository.TeacherRepository;
import com.group.Timetable.Generator.Repository.UserRepository;
import com.group.Timetable.Generator.dto.TeacherDTO;
import com.group.Timetable.Generator.entities.Department;
import com.group.Timetable.Generator.entities.Teacher;
import com.group.Timetable.Generator.entities.User;
import com.group.Timetable.Generator.mapper.TeacherMapper;

@Service
public class TeacherService {

    private final TeacherRepository teacherRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    public TeacherService(TeacherRepository teacherRepository,
                          DepartmentRepository departmentRepository,
                          UserRepository userRepository) {
        this.teacherRepository = teacherRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
    }

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }

    // ================= CREATE =================
    public TeacherDTO saveTeacher(TeacherDTO dto) {
        User user = getAuthenticatedUser();

        Department dept = departmentRepository
                .findByDeptNameAndUser(dto.getDepartmentName(), user)
                .orElseThrow(() -> new RuntimeException("Department not found for this user"));

        // Duplicate teacher check
        if (teacherRepository.findByTeacherNameIgnoreCaseAndUser(dto.getTeacherName(), user).isPresent()) {
            throw new RuntimeException("Teacher already exists for this user");
        }

        Teacher teacher = Teacher.builder()
                .teacherName(dto.getTeacherName())
                .teacherEmail(dto.getTeacherEmail())
                .department(dept)
                .user(user)
                .build();

        return TeacherMapper.toDTO(teacherRepository.save(teacher));
    }

    // ================= GET ALL =================
    public List<TeacherDTO> getTeachersForAuthenticatedUser() {
        User user = getAuthenticatedUser();

        return teacherRepository.findByUser(user)
                .stream()
                .map(TeacherMapper::toDTO)
                .toList();
    }

    // ================= GET BY ID =================
    public TeacherDTO getTeacherById(Long id) {
        User user = getAuthenticatedUser();

        Teacher teacher = teacherRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Teacher not found for this user"));

        return TeacherMapper.toDTO(teacher);
    }

    // ================= UPDATE =================
    public TeacherDTO updateTeacher(Long id, TeacherDTO dto) {
        User user = getAuthenticatedUser();

        Teacher teacher = teacherRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Teacher not found for this user"));

        Department dept = departmentRepository
                .findByDeptNameAndUser(dto.getDepartmentName(), user)
                .orElseThrow(() -> new RuntimeException("Department not found for this user"));

        // Duplicate check on update
        teacherRepository.findByTeacherNameIgnoreCaseAndUser(dto.getTeacherName(), user)
                .filter(t -> !t.getTeacherId().equals(id))
                .ifPresent(t -> {
                    throw new RuntimeException("Another teacher with same name exists");
                });

        teacher.setTeacherName(dto.getTeacherName());
        teacher.setTeacherEmail(dto.getTeacherEmail());
        teacher.setDepartment(dept);

        return TeacherMapper.toDTO(teacherRepository.save(teacher));
    }

    // ================= DELETE =================
    public void deleteTeacher(Long id) {
        User user = getAuthenticatedUser();

        Teacher teacher = teacherRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new RuntimeException("Teacher not found for this user"));

        teacherRepository.delete(teacher);
    }

    // ================= SEARCH =================
    public List<TeacherDTO> searchByName(String name) {
        User user = getAuthenticatedUser();

        return teacherRepository
                .findByTeacherNameContainingIgnoreCaseAndUser(name, user)
                .stream()
                .map(TeacherMapper::toDTO)
                .toList();
    }

    // ================= FIND BY DEPT =================
    public List<TeacherDTO> findByDepartmentName(String deptName) {
        User user = getAuthenticatedUser();

        Department dept = departmentRepository
                .findByDeptNameAndUser(deptName, user)
                .orElseThrow(() -> new RuntimeException("Department not found for this user"));

        return teacherRepository.findByDepartmentAndUser(dept, user)
                .stream()
                .map(TeacherMapper::toDTO)
                .toList();
    }
}
