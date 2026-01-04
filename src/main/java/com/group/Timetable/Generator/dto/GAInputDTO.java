package com.group.Timetable.Generator.dto;

import lombok.*;

import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GAInputDTO {

    private List<GASectionDTO> sections;
    private List<GASubjectDTO> subjects;
    private List<GATeacherDTO> teachers;
    private List<GATeacherSubjectDTO> teacherSubjectMappings;
    private List<GARoomDTO> rooms;
    private List<GATimeSlotDTO> timeSlots;

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class GASectionDTO {
        private Long sectionId;
        private Integer semester;
        private String sectionName;
        private Long courseId;
        private String courseName;
        private String branch; 
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class GASubjectDTO {
        private Long subjectId;
        private String subName;
        private Integer subCredit;
        private String subType;
        private Long courseId;
        private Integer semester; 
        private String branch; 
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class GATeacherDTO {
        private Long teacherId;
        private String teacherName;
        private String teacherEmail;
        private Long departmentId;
        private String departmentName;
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class GATeacherSubjectDTO {
        private Long mappingId;
        private Long teacherId;
        private Long subjectId;
        private Long sectionId; 
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class GARoomDTO {
        private Long roomId;
        private String roomName;
        private String roomType; 
    }

    @Data @NoArgsConstructor @AllArgsConstructor @Builder
    public static class GATimeSlotDTO {
        private Long slotId;
        private String day;
        private String startTime;
        private String endTime;
        private Integer slotNumber;
    }
}
