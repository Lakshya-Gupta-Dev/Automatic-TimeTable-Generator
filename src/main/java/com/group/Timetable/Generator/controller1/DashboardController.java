package com.group.Timetable.Generator.controller1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.group.Timetable.Generator.Repository.CoursesRepository;
import com.group.Timetable.Generator.Repository.DepartmentRepository;
import com.group.Timetable.Generator.Repository.TeacherRepository;

@RestController
@RequestMapping("/api/user/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private CoursesRepository coursesRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @GetMapping("/classes/count")
    public long getCourseCount() {
        return coursesRepository.count();
    }

    @GetMapping("/departments/count")
    public long getDepartmentCount() {
        return departmentRepository.count();
    }

    @GetMapping("/teachers/count")
    public long getTeacherCount() {
        return teacherRepository.count();
    }
}

