package com.group.Timetable.Generator.Repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.group.Timetable.Generator.entities.Room;
import com.group.Timetable.Generator.entities.User;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    List<Room> findByUser(User user);

    List<Room> findByRoomNameContainingIgnoreCaseAndUser(String roomName, User user);

    Optional<Room> findByRoomIdAndUser(Long roomId, User user);

    Optional<Room> findByRoomNameAndUser(String roomName, User user);
}
