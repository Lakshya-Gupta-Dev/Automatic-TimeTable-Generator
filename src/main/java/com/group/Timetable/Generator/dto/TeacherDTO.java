package com.group.Timetable.Generator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TeacherDTO {

    private Long teacherId;
    private String teacherName;
    private String teacherEmail;
    private String departmentName; 
    private String username;
}
