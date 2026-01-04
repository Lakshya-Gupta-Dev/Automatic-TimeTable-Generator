package com.group.Timetable.Generator.ga;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Single lecture assignment (gene).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GAGene {
    private Long sectionId;   // Section id
    private Long subjectId;   // Subject id
    private Long teacherId;   // Teacher id
    private Long roomId;      // Room id
    private Long timeSlotId;  // TimeSlot id
}












//package com.group.Timetable.Generator.ga;
//
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
///**
// * Single lecture assignment (gene).
// */
//@Data
//@AllArgsConstructor
//@NoArgsConstructor
//public class GAGene {
//    private Long sectionId;   // Section id
//    private Long subjectId;   // Subject id
//    private Long teacherId;   // Teacher id
//    private Long roomId;      // Room id
//    private Long timeSlotId;  // TimeSlot id
//}
