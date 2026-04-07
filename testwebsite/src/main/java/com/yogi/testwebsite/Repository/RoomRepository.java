package com.yogi.testwebsite.Repository;

import com.yogi.testwebsite.Entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByAvailableTrue();
    List<Room> findByType(String type);
    List<Room> findByMaxGuestsGreaterThanEqual(int guests);
    List<Room> findByPricePerNightLessThanEqual(double maxPrice);
}
