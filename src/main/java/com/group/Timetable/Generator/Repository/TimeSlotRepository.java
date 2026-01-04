package com.group.Timetable.Generator.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group.Timetable.Generator.entities.TimeSlot;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    List<TimeSlot> findByUserId(Long userId);

    boolean existsByUserIdAndDayAndSlotNumber(Long userId, String day, Integer slotNumber);

    boolean existsByUserIdAndDayAndStartTimeLessThanAndEndTimeGreaterThan(
            Long userId, String day, java.time.LocalTime endTime, java.time.LocalTime startTime
    );
}

