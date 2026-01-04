
package com.group.Timetable.Generator.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.group.Timetable.Generator.Repository.TimeSlotRepository;
import com.group.Timetable.Generator.entities.TimeSlot;
import com.group.Timetable.Generator.entities.User;

@Service
@Transactional
public class TimeSlotService {

    private final TimeSlotRepository repo;

    public TimeSlotService(TimeSlotRepository repo) {
        this.repo = repo;
    }

    public TimeSlot create(TimeSlot slot, User user) {
        // set owner
        slot.setUser(user);

        // unique validation
        if (repo.existsByUserIdAndDayAndSlotNumber(user.getId(), slot.getDay(), slot.getSlotNumber())) {
            throw new RuntimeException("Slot number already exists for this user on this day.");
        }

        // overlap validation
        if (repo.existsByUserIdAndDayAndStartTimeLessThanAndEndTimeGreaterThan(
                user.getId(), slot.getDay(), slot.getEndTime(), slot.getStartTime()
        )) {
            throw new RuntimeException("Timeslot overlaps with an existing slot for this user.");
        }

        return repo.save(slot);
    }

    public TimeSlot update(Long id, TimeSlot updatedSlot, User user) {
        TimeSlot existing = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("TimeSlot not found"));

        // ownership check
        if (!existing.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        // unique check
        if (!existing.getSlotNumber().equals(updatedSlot.getSlotNumber())) {
            if (repo.existsByUserIdAndDayAndSlotNumber(user.getId(), updatedSlot.getDay(), updatedSlot.getSlotNumber())) {
                throw new RuntimeException("Slot number already exists.");
            }
        }

        // overlap validation
        boolean overlap = repo.existsByUserIdAndDayAndStartTimeLessThanAndEndTimeGreaterThan(
                user.getId(), updatedSlot.getDay(), updatedSlot.getEndTime(), updatedSlot.getStartTime()
        );
        if (overlap) {
            throw new RuntimeException("Timeslot overlaps with another slot.");
        }

        existing.setDay(updatedSlot.getDay());
        existing.setStartTime(updatedSlot.getStartTime());
        existing.setEndTime(updatedSlot.getEndTime());
        existing.setSlotNumber(updatedSlot.getSlotNumber());
        existing.setUser(user);

        return repo.save(existing);
    }

    public void delete(Long id, User user) {
        TimeSlot ts = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("TimeSlot not found"));

        if (!ts.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        repo.delete(ts);
    }

    public TimeSlot getById(Long id, User user) {
        TimeSlot ts = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("TimeSlot not found"));

        if (!ts.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        return ts;
    }

    public List<TimeSlot> list(User user) {
        return repo.findByUserId(user.getId());
    }
}
