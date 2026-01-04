package com.group.Timetable.Generator.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group.Timetable.Generator.entities.Department;
import com.group.Timetable.Generator.entities.User;
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    // 1) Search by name but only for one user
    List<Department> findByDeptNameContainingIgnoreCaseAndUser(String deptName, User user);

    // 2) Find by dept head only for particular user
    List<Department> findByDeptHeadAndUser(String deptHead, User user);

    // 3) Find exact name but only for one user
    Optional<Department> findByDeptNameAndUser(String deptName, User user);

    // 4) Find all departments belonging to a user
    List<Department> findByUser(User user);
}
