
package com.group.Timetable.Generator.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.group.Timetable.Generator.entities.Department;
import com.group.Timetable.Generator.entities.Teacher;
import com.group.Timetable.Generator.entities.User;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {

 
    List<Teacher> findByTeacherNameContainingIgnoreCaseAndUser(String teacherName, User user);

   
    Optional<Teacher> findByTeacherNameIgnoreCaseAndUser(String teacherName, User user);

  
    List<Teacher> findByDepartmentAndUser(Department department, User user);

 
    List<Teacher> findByUser(User user);
}
