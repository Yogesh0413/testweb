package com.yogi.testwebsite.Controller;

import com.yogi.testwebsite.Entity.Room;
import com.yogi.testwebsite.Service.RoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/rooms")
public class RoomController {

    private final RoomService roomService;

    public RoomController(RoomService roomService) {
        this.roomService = roomService;
    }

    // GET /rooms — show all available rooms, with optional filters
    @GetMapping
    public String listRooms(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer guests,
            @RequestParam(required = false) Double maxPrice,
            Model model) {

        List<Room> rooms;

        if (type != null && !type.isBlank()) {
            rooms = roomService.getRoomsByType(type);
        } else if (guests != null) {
            rooms = roomService.getRoomsForGuests(guests);
        } else if (maxPrice != null) {
            rooms = roomService.getRoomsUnderPrice(maxPrice);
        } else {
            rooms = roomService.getAvailableRooms();
        }

        model.addAttribute("rooms", rooms);
        model.addAttribute("selectedType", type);
        model.addAttribute("selectedGuests", guests);
        model.addAttribute("selectedMaxPrice", maxPrice);
        model.addAttribute("hotelName", "The Grand Horizon");
        model.addAttribute("pageTitle", "Rooms & Suites");
        model.addAttribute("activePage", "rooms");

        return "rooms";
    }

    // GET /rooms/{id} — single room detail page
    @GetMapping("/{id}")
    public String roomDetail(@PathVariable Long id, Model model) {
        Optional<Room> roomOpt = roomService.getRoomById(id);

        if (roomOpt.isEmpty()) {
            return "redirect:/rooms";
        }

        Room room = roomOpt.get();
        List<Room> otherRooms = roomService.getAvailableRooms()
                .stream()
                .filter(r -> !r.getId().equals(id))
                .limit(3)
                .toList();

        model.addAttribute("room", room);
        model.addAttribute("otherRooms", otherRooms);
        model.addAttribute("hotelName", "The Grand Horizon");
        model.addAttribute("pageTitle", room.getName());
        model.addAttribute("activePage", "rooms");

        return "room-detail";
    }
}
