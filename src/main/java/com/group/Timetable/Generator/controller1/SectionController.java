package com.group.Timetable.Generator.controller1;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.group.Timetable.Generator.dto.SectionDTO;
import com.group.Timetable.Generator.service.SectionService;

@RestController
@RequestMapping("/api/user/section")
@CrossOrigin(origins = "*")
public class SectionController {

    private final SectionService service;

    public SectionController(SectionService service) {
        this.service = service;
    }

    @PostMapping("/save")
    public ResponseEntity<SectionDTO> save(@RequestBody SectionDTO dto) {
        return ResponseEntity.ok(service.save(dto));
    }

    @GetMapping
    public ResponseEntity<List<SectionDTO>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SectionDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<SectionDTO> update(@PathVariable Long id, @RequestBody SectionDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Section deleted");
    }
}

