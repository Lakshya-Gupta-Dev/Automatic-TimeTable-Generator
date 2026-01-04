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

import com.group.Timetable.Generator.dto.CourseDTO;
import com.group.Timetable.Generator.service.CourseService;

@RestController
@RequestMapping("/api/user/course")
@CrossOrigin(origins = "*")
public class CoursesController {

    private final CourseService courseService;

    public CoursesController(CourseService courseService) {
        this.courseService = courseService;
    }

    @PostMapping("/save")
    public ResponseEntity<CourseDTO> saveCourse(@RequestBody CourseDTO courseDTO) {
        return ResponseEntity.ok(courseService.saveCourse(courseDTO));
    }

    @GetMapping
    public ResponseEntity<List<CourseDTO>> getAllCourses() {
        return ResponseEntity.ok(courseService.getCoursesForAuthenticatedUser());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<CourseDTO> updateCourse(
            @PathVariable Long id,
            @RequestBody CourseDTO courseDTO) {
        return ResponseEntity.ok(courseService.updateCourse(id, courseDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/{name}")
    public ResponseEntity<List<CourseDTO>> searchByName(@PathVariable String name) {
        return ResponseEntity.ok(courseService.searchByName(name));
    }

    @GetMapping("/department/{deptName}")
    public ResponseEntity<List<CourseDTO>> findByDepartment(@PathVariable String deptName) {
        return ResponseEntity.ok(courseService.findByDepartmentName(deptName));
    }

    @GetMapping("/names")
    public ResponseEntity<List<String>> getCourseNamesForUser() {
        List<CourseDTO> courses = courseService.getCoursesForAuthenticatedUser();
        List<String> courseNames = courses.stream()
                .map(CourseDTO::getCourseName)
                .toList();
        return ResponseEntity.ok(courseNames);
    }
}

