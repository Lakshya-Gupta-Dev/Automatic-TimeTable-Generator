package com.group.Timetable.Generator.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.group.Timetable.Generator.entities.*;

public interface TeacherSubjectMappingRepository 
        extends JpaRepository<TeacherSubjectMapping, Long> {

    List<TeacherSubjectMapping> findByUser(User user);

    List<TeacherSubjectMapping> findByTeacherAndUser(Teacher teacher, User user);

    List<TeacherSubjectMapping> findBySubjectAndUser(Subjects subject, User user);

    List<TeacherSubjectMapping> findBySectionAndUser(Section section, User user);

    Optional<TeacherSubjectMapping> findByTeacherAndSubjectAndSectionAndUser(
            Teacher teacher, Subjects subject, Section section, User user
    );

    List<TeacherSubjectMapping> findByTeacherTeacherNameContainingIgnoreCaseAndUser(
            String teacherName, User user
    );
}

















//package com.group.Timetable.Generator.Repository;
//
//import java.util.List;
//import java.util.Optional;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import com.group.Timetable.Generator.entities.Subjects;
//import com.group.Timetable.Generator.entities.Teacher;
//import com.group.Timetable.Generator.entities.TeacherSubjectMapping;
//import com.group.Timetable.Generator.entities.User;
//
//@Repository
//public interface TeacherSubjectMappingRepository extends JpaRepository<TeacherSubjectMapping, Long> {
//
//    List<TeacherSubjectMapping> findByUser(User user);
//
//    List<TeacherSubjectMapping> findByTeacherAndUser(Teacher teacher, User user);
//
//    List<TeacherSubjectMapping> findBySubjectAndUser(Subjects subject, User user);
//
//    Optional<TeacherSubjectMapping> findByTeacherAndSubjectAndUser(Teacher teacher, Subjects subject, User user);
//
//    List<TeacherSubjectMapping> findByTeacherTeacherNameContainingIgnoreCaseAndUser(String teacherName, User user);
//}
