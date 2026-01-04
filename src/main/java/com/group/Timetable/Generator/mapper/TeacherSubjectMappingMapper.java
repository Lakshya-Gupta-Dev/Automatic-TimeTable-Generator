package com.group.Timetable.Generator.mapper;

import com.group.Timetable.Generator.dto.TeacherSubjectMappingDTO;
import com.group.Timetable.Generator.entities.TeacherSubjectMapping;

public class TeacherSubjectMappingMapper {

    public static TeacherSubjectMappingDTO toDTO(TeacherSubjectMapping e) {
        if (e == null) return null;
        return TeacherSubjectMappingDTO.builder()
                .id(e.getId())
                .teacherId(e.getTeacher() != null ? e.getTeacher().getTeacherId() : null)
                .teacherName(e.getTeacher() != null ? e.getTeacher().getTeacherName() : null)
                .subjectId(e.getSubject() != null ? e.getSubject().getSubId() : null)
                .subjectName(e.getSubject() != null ? e.getSubject().getSubName() : null)
                .sectionId(e.getSection() != null ? e.getSection().getSectionId() : null)
                .sectionName(e.getSection() != null ? e.getSection().getSectionName() : null)
                .sectionBranch(e.getSection() != null ? e.getSection().getBranch() : null)
                .sectionSemester(e.getSection() != null ? e.getSection().getSemester() : null)
                .courseName(e.getSection() != null && e.getSection().getCourse() != null
                        ? e.getSection().getCourse().getCourseName() : null)
                .build();
    }

    public static TeacherSubjectMapping toEntity(TeacherSubjectMappingDTO dto,
                                                 com.group.Timetable.Generator.entities.Teacher teacher,
                                                 com.group.Timetable.Generator.entities.Subjects subject,
                                                 com.group.Timetable.Generator.entities.Section section,
                                                 com.group.Timetable.Generator.entities.User user) {
        if (dto == null) return null;
        return TeacherSubjectMapping.builder()
                .id(dto.getId())
                .teacher(teacher)
                .subject(subject)
                .section(section)
                .user(user)
                .build();
    }
}

