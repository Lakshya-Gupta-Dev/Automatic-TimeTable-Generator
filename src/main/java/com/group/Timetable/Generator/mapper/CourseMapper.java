
package com.group.Timetable.Generator.mapper;

import com.group.Timetable.Generator.dto.CourseDTO;
import com.group.Timetable.Generator.entities.Courses;

public class CourseMapper {

    public static CourseDTO toDTO(Courses course) {
        return new CourseDTO(
                course.getCourseId(),
                course.getCourseName(),
                course.getSemester(),
                course.getDepartment() != null ? course.getDepartment().getDeptName() : null,
                course.getUser() != null ? course.getUser().getUsername() : null);
    }
}
