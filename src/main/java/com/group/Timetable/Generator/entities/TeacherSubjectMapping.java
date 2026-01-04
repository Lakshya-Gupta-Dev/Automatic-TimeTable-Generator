package com.group.Timetable.Generator.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "teacher_subject_mapping",
       uniqueConstraints = @UniqueConstraint(
               columnNames = {"teacher_id", "subject_id", "section_id", "user_id"}
       ))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherSubjectMapping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subjects subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
