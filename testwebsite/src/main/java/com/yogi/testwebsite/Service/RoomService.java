package com.yogi.testwebsite.Service;

import com.yogi.testwebsite.Entity.Room;
import com.yogi.testwebsite.Repository.RoomRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoomService {

    private final RoomRepository roomRepository;

    public RoomService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    // Seed the DB with sample rooms on startup
    @PostConstruct
    public void seedRooms() {
        if (roomRepository.count() == 0) {
            roomRepository.saveAll(List.of(
                new Room("Deluxe Garden Room", "Deluxe",
                    "A refined retreat surrounded by lush gardens. Features a king bed, marble bathroom, and private terrace overlooking manicured grounds.",
                    220.00, 2, 1, "King", 38, true,
                    "https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800&q=80",
                    "Garden"),

                new Room("Ocean View Suite", "Suite",
                    "Wake up to the sound of waves. This expansive suite offers floor-to-ceiling ocean views, a separate living area, and a soaking tub.",
                    420.00, 2, 1, "King", 65, true,
                    "https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800&q=80",
                    "Ocean"),

                new Room("Grand Pool Villa", "Villa",
                    "The pinnacle of luxury. A private villa with your own plunge pool, outdoor shower, personal butler service, and panoramic horizon views.",
                    850.00, 4, 2, "King", 120, true,
                    "https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?w=800&q=80",
                    "Pool"),

                new Room("Classic Double Room", "Classic",
                    "Thoughtfully designed for comfort and simplicity. Two double beds, warm lighting, and easy access to resort amenities.",
                    160.00, 4, 2, "Double", 30, true,
                    "https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=800&q=80",
                    "Garden"),

                new Room("Horizon Penthouse", "Penthouse",
                    "The crown jewel of The Grand Horizon. A full-floor penthouse with wraparound terrace, private dining room, and butler on call 24/7.",
                    1800.00, 6, 3, "King", 280, true,
                    "https://images.unsplash.com/photo-1560185007-c5ca9d2c014d?w=800&q=80",
                    "Panoramic"),

                new Room("Sunset Terrace Suite", "Suite",
                    "Golden evenings await. Designed to face the setting sun, this suite features a wraparound terrace, twin loungers, and warm teak finishes.",
                    480.00, 2, 1, "King", 72, true,
                    "https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=800&q=80",
                    "Ocean")
            ));
        }
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    public List<Room> getAvailableRooms() {
        return roomRepository.findByAvailableTrue();
    }

    public Optional<Room> getRoomById(Long id) {
        return roomRepository.findById(id);
    }

    public List<Room> getRoomsByType(String type) {
        return roomRepository.findByType(type);
    }

    public List<Room> getRoomsForGuests(int guests) {
        return roomRepository.findByMaxGuestsGreaterThanEqual(guests);
    }

    public List<Room> getRoomsUnderPrice(double maxPrice) {
        return roomRepository.findByPricePerNightLessThanEqual(maxPrice);
    }

    public Room saveRoom(Room room) {
        return roomRepository.save(room);
    }
}
