package com.group.Timetable.Generator.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.group.Timetable.Generator.entities.Courses;
import com.group.Timetable.Generator.entities.Subjects;
import com.group.Timetable.Generator.entities.User;

@Repository
public interface SubjectRepository extends JpaRepository<Subjects, Long> {

    // Get all subjects of logged in user
    List<Subjects> findByUser(User user);

    // Search subject name for specific user
    List<Subjects> findBySubNameContainingIgnoreCaseAndUser(String subName, User user);

    // All subjects of a course for specific user
    List<Subjects> findByCoursesAndUser(Courses course, User user);

    // Duplicate check: same subject name, same course, same user
    Optional<Subjects> findBySubNameIgnoreCaseAndCoursesAndUser(String subName, Courses course, User user);
    
    @Query("SELECT s FROM Subjects s WHERE (s.branch = :branch OR s.branch = 'ALL') AND s.user = :user")
    List<Subjects> findByBranchOrAll(String branch, User user);

}

