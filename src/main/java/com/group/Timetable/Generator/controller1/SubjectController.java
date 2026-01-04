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

import com.group.Timetable.Generator.dto.SubjectDTO;
import com.group.Timetable.Generator.service.SubjectService;

@RestController
@RequestMapping("/api/user/subjects")
@CrossOrigin(origins = "*")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    // CREATE
    @PostMapping("/save")
    public ResponseEntity<SubjectDTO> saveSubject(@RequestBody SubjectDTO subjectDTO) {
        return ResponseEntity.ok(subjectService.saveSubject(subjectDTO));
    }

    // READ all subjects for logged-in user
    @GetMapping
    public ResponseEntity<List<SubjectDTO>> getAllSubjects() {
        return ResponseEntity.ok(subjectService.getAllSubjectsForUser());
    }

    // READ by ID
    @GetMapping("/{id}")
    public ResponseEntity<SubjectDTO> getSubjectById(@PathVariable Long id) {
        return ResponseEntity.ok(subjectService.getSubjectById(id));
    }

    // UPDATE
    @PutMapping("/update/{id}")
    public ResponseEntity<SubjectDTO> updateSubject(@PathVariable Long id, @RequestBody SubjectDTO subjectDTO) {
        return ResponseEntity.ok(subjectService.updateSubject(id, subjectDTO));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSubject(@PathVariable Long id) {
        subjectService.deleteSubject(id);
        return ResponseEntity.ok("Subject deleted successfully!");
    }

    // SEARCH by name
    @GetMapping("/search/{name}")
    public ResponseEntity<List<SubjectDTO>> searchByName(@PathVariable String name) {
        return ResponseEntity.ok(subjectService.searchByName(name));
    }

    // FIND subjects by course
    @GetMapping("/course/{courseName}")
    public ResponseEntity<List<SubjectDTO>> findByCourse(@PathVariable String courseName) {
        return ResponseEntity.ok(subjectService.findByCourseName(courseName));
    }
}

