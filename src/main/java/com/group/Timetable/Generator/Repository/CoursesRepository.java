package com.group.Timetable.Generator.Repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group.Timetable.Generator.entities.Courses;
import com.group.Timetable.Generator.entities.Department;
import com.group.Timetable.Generator.entities.User;
@Repository
public interface CoursesRepository extends JpaRepository<Courses, Long> {

  
    List<Courses> findByCourseNameContainingIgnoreCaseAndUser(String courseName, User user);

    
    Optional<Courses> findByCourseNameIgnoreCaseAndUser(String courseName, User user);


    List<Courses> findByDepartmentAndUser(Department department, User user);

    
    List<Courses> findByUser(User user);


    List<Courses> findAllByCourseNameAndUser(String courseName, User user);
}

