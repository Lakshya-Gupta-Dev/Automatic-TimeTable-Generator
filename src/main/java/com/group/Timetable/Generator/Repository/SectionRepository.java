package com.group.Timetable.Generator.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group.Timetable.Generator.entities.Courses;
import com.group.Timetable.Generator.entities.Section;
import com.group.Timetable.Generator.entities.User;

@Repository
public interface SectionRepository extends JpaRepository<Section, Long> {

    List<Section> findByUser(User user);

    List<Section> findByCourseAndUser(Courses course, User user);

    Optional<Section> findBySectionNameIgnoreCaseAndSemesterAndCourseAndUser(
            String sectionName,
            Integer semester,
            Courses course,
            User user
    );
    
    Optional<Section> findBySectionNameIgnoreCaseAndSemesterAndCourseAndBranchIgnoreCaseAndUser(
            String sectionName,
            Integer semester,
            Courses course,
            String branch,
            User user
    );

    
    List<Section> findByBranchAndUser(String branch, User user);

}
