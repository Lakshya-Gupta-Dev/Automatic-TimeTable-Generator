package com.group.Timetable.Generator.mapper;

import com.group.Timetable.Generator.dto.SectionDTO;
import com.group.Timetable.Generator.entities.Courses;
import com.group.Timetable.Generator.entities.Section;
import com.group.Timetable.Generator.entities.User;

public class SectionMapper {

    public static SectionDTO toDTO(Section s) {
        return SectionDTO.builder()
                .sectionId(s.getSectionId())
                .semester(s.getSemester())
                .sectionName(s.getSectionName())
                .branch(s.getBranch())
                .courseId(s.getCourse() != null ? s.getCourse().getCourseId() : null)
                .courseName(s.getCourse() != null ? s.getCourse().getCourseName() : null)
                .build();
    }

    public static Section toEntity(SectionDTO dto, Courses course, User user) {
        return Section.builder()
                .sectionId(dto.getSectionId())
                .semester(dto.getSemester())
                .sectionName(dto.getSectionName())
                .branch(dto.getBranch())
                .course(course)
                .user(user)
                .build();
    }
}

