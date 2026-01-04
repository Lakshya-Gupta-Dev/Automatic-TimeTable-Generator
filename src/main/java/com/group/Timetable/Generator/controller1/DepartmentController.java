package com.group.Timetable.Generator.controller1;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.group.Timetable.Generator.dto.DepartmentDTO;
import com.group.Timetable.Generator.service.DepartmentService;

import lombok.RequiredArgsConstructor;
@RestController
@RequestMapping("/api/user/departments")
@RequiredArgsConstructor
@CrossOrigin("*")
public class DepartmentController {

    private final DepartmentService departmentService;

    @GetMapping
    public ResponseEntity<List<DepartmentDTO>> getDepartments() {
        return ResponseEntity.ok(departmentService.getDepartmentsForAuthenticatedUser());
    }

    @PostMapping
    public ResponseEntity<DepartmentDTO> add(@RequestBody DepartmentDTO dto) {
        return ResponseEntity.ok(departmentService.addDepartment(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartmentDTO> update(@PathVariable Long id, @RequestBody DepartmentDTO dto) {
        return ResponseEntity.ok(departmentService.updateDepartment(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok("Deleted");
    }

    // SEARCH NAME
    @GetMapping("/search/name/{name}")
    public ResponseEntity<List<DepartmentDTO>> searchByName(@PathVariable String name) {
        return ResponseEntity.ok(departmentService.searchDepartmentByName(name));
    }

    // SEARCH HEAD
    @GetMapping("/search/head/{head}")
    public ResponseEntity<List<DepartmentDTO>> searchByHead(@PathVariable String head) {
        return ResponseEntity.ok(departmentService.searchDepartmentByHead(head));
    }
    
    @GetMapping("/names")
    public ResponseEntity<List<String>> getDepartmentNames() {
        return ResponseEntity.ok(departmentService.getDepartmentNames());
    }

}






