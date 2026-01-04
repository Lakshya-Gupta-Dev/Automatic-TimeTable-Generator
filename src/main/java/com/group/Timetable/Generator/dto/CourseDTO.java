package com.group.Timetable.Generator.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CourseDTO {


private Long courseId;
private String courseName;
private int semester;
private String departmentName;
private String username;
}
