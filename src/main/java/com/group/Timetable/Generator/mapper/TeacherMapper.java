package com.group.Timetable.Generator.mapper;

import com.group.Timetable.Generator.dto.TeacherDTO;
import com.group.Timetable.Generator.entities.Teacher;

public class TeacherMapper {

    public static TeacherDTO toDTO(Teacher teacher) {
        return new TeacherDTO(
                teacher.getTeacherId(),
                teacher.getTeacherName(),
                teacher.getTeacherEmail(),
                teacher.getDepartment() != null ? teacher.getDepartment().getDeptName() : null,
                teacher.getUser() != null ? teacher.getUser().getUsername() : null
        );
    }
}
