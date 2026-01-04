package com.group.Timetable.Generator.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.group.Timetable.Generator.Repository.DepartmentRepository;
import com.group.Timetable.Generator.Repository.UserRepository;
import com.group.Timetable.Generator.dto.DepartmentDTO;
import com.group.Timetable.Generator.entities.Department;
import com.group.Timetable.Generator.entities.User;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;

    private User getAuthenticatedUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Authenticated user not found"));
    }


    // ========== GET ALL ==============
    public List<DepartmentDTO> getDepartmentsForAuthenticatedUser() {
        User user = getAuthenticatedUser();

        return departmentRepository.findByUser(user)
                .stream()
                .map(dept -> DepartmentDTO.builder()
                        .id(dept.getDeptId())
                        .deptName(dept.getDeptName())
                        .deptHead(dept.getDeptHead())
                        .username(user.getUsername())
                        .build())
                .toList();
    }


    // ========== ADD ==============
    public DepartmentDTO addDepartment(DepartmentDTO dto) {
        User user = getAuthenticatedUser();

        // Duplicate per user check
        if (departmentRepository.findByDeptNameAndUser(dto.getDeptName(), user).isPresent()) {
            throw new RuntimeException("Department already exists for this user");
        }

        Department dept = Department.builder()
                .deptName(dto.getDeptName())
                .deptHead(dto.getDeptHead())
                .user(user)
                .build();

        Department saved = departmentRepository.save(dept);

        return DepartmentDTO.builder()
                .id(saved.getDeptId())
                .deptName(saved.getDeptName())
                .deptHead(saved.getDeptHead())
                .username(user.getUsername())
                .build();
    }


    // ========== UPDATE ==============
    public DepartmentDTO updateDepartment(Long id, DepartmentDTO dto) {
        User user = getAuthenticatedUser();

        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        if (!dept.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        // Duplicate check while updating
        departmentRepository.findByDeptNameAndUser(dto.getDeptName(), user)
                .filter(d -> !d.getDeptId().equals(id))
                .ifPresent(d -> {
                    throw new RuntimeException("Another department with same name exists for this user");
                });

        dept.setDeptName(dto.getDeptName());
        dept.setDeptHead(dto.getDeptHead());

        Department updated = departmentRepository.save(dept);

        return DepartmentDTO.builder()
                .id(updated.getDeptId())
                .deptName(updated.getDeptName())
                .deptHead(updated.getDeptHead())
                .username(user.getUsername())
                .build();
    }


    // ========== DELETE ==============
    public void deleteDepartment(Long id) {
        User user = getAuthenticatedUser();

        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));

        if (!dept.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized delete attempt");
        }

        departmentRepository.delete(dept);
    }


    // ========== SEARCH BY NAME (user-specific) ==============
    public List<DepartmentDTO> searchDepartmentByName(String name) {
        User user = getAuthenticatedUser();

        return departmentRepository
                .findByDeptNameContainingIgnoreCaseAndUser(name, user)
                .stream()
                .map(d -> new DepartmentDTO(
                        d.getDeptId(),
                        d.getDeptName(),
                        d.getDeptHead(),
                        user.getUsername()
                ))
                .toList();
    }

    // ========== SEARCH BY HEAD (user-specific) ==============
    public List<DepartmentDTO> searchDepartmentByHead(String head) {
        User user = getAuthenticatedUser();

        return departmentRepository.findByDeptHeadAndUser(head, user)
                .stream()
                .map(d -> new DepartmentDTO(
                        d.getDeptId(),
                        d.getDeptName(),
                        d.getDeptHead(),
                        user.getUsername()
                ))
                .toList();
    }
    
    public List<String> getDepartmentNames() {
        User user = getAuthenticatedUser();
        return departmentRepository.findByUser(user)
                .stream()
                .map(Department::getDeptName)
                .toList();
    }

}
