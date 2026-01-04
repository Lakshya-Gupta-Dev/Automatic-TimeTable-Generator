package com.group.Timetable.Generator.controller1;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.group.Timetable.Generator.dto.TeacherSubjectMappingDTO;
import com.group.Timetable.Generator.service.TeacherSubjectMappingService;

@RestController
@RequestMapping("/api/user/teacher-subject")
@CrossOrigin(origins="*")
public class TeacherSubjectMappingController {

    private final TeacherSubjectMappingService service;
    public TeacherSubjectMappingController(TeacherSubjectMappingService service){ this.service=service; }

    @PostMapping("/save")
    public ResponseEntity<TeacherSubjectMappingDTO> save(@RequestBody TeacherSubjectMappingDTO dto){
        return ResponseEntity.ok(service.save(dto));
    }

    @GetMapping
    public ResponseEntity<List<TeacherSubjectMappingDTO>> getAll(){
        return ResponseEntity.ok(service.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TeacherSubjectMappingDTO> getById(@PathVariable Long id){
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<TeacherSubjectMappingDTO> update(@PathVariable Long id, @RequestBody TeacherSubjectMappingDTO dto){
        return ResponseEntity.ok(service.update(id,dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.ok("Mapping deleted");
    }

    @GetMapping("/search/teacher/{name}")
    public ResponseEntity<List<TeacherSubjectMappingDTO>> searchByTeacher(@PathVariable String name){
        return ResponseEntity.ok(service.searchByTeacherName(name));
    }
}
