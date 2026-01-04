package com.group.Timetable.Generator.Repository;

import com.group.Timetable.Generator.entities.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // JPA derived query to find schedules by user ID
    List<Schedule> findByUserId(Long userId);

    // JPA derived query to delete schedules by user ID
    void deleteByUserId(Long userId);
    

    @Query("SELECT COUNT(DISTINCT s.generationId) FROM Schedule s")
    long countDistinctByGenerationId();
    
    @Query("SELECT s.user.id, s.user.username, s.user.instituteName, COUNT(DISTINCT s.generationId) " +
    	       "FROM Schedule s GROUP BY s.user.id, s.user.username, s.user.instituteName")
    	List<Object[]> getTimetableCountsByUsers();

    	List<Schedule> findByUserIdAndTeacher_TeacherNameContainingIgnoreCaseOrUserIdAndSubject_SubNameContainingIgnoreCaseOrUserIdAndSection_SectionNameContainingIgnoreCaseOrUserIdAndRoom_RoomNameContainingIgnoreCaseOrUserIdAndTimeSlot_DayContainingIgnoreCase(
    	        Long userId1, String teacher,
    	        Long userId2, String subject,
    	        Long userId3, String section,
    	        Long userId4, String room,
    	        Long userId5, String day
    	);

}

