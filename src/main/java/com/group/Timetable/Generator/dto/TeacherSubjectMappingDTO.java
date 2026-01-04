package com.group.Timetable.Generator.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherSubjectMappingDTO {

    private Long id;

    private Long teacherId;
    private String teacherName;

    private Long subjectId;
    private String subjectName;

    private Long sectionId;
    private String sectionName;
    private Integer sectionSemester;
    private String sectionBranch;
    
    private String courseName; 
}




















//package com.group.Timetable.Generator.dto;
//
//import lombok.*;
//
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Builder
//public class TeacherSubjectMappingDTO {
//    private Long id;
//
//    private Long teacherId;
//    private String teacherName; // optional for display
//
//    private Long subjectId;
//    private String subjectName; // optional for display
//}

