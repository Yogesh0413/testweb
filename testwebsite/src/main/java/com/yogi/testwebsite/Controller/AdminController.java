package com.yogi.testwebsite.Controller;

import com.yogi.testwebsite.Entity.Booking;
import com.yogi.testwebsite.Entity.Room;
import com.yogi.testwebsite.Service.BookingService;
import com.yogi.testwebsite.Service.RoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final BookingService bookingService;
    private final RoomService roomService;

    public AdminController(BookingService bookingService, RoomService roomService) {
        this.bookingService = bookingService;
        this.roomService    = roomService;
    }

    // GET /admin — dashboard overview
    @GetMapping
    public String dashboard(Model model) {
        List<Booking> bookings = bookingService.getAllBookings();
        List<Room>    rooms    = roomService.getAllRooms();

        long confirmed  = bookings.stream().filter(b -> b.getStatus() == Booking.Status.CONFIRMED).count();
        long pending    = bookings.stream().filter(b -> b.getStatus() == Booking.Status.PENDING).count();
        long cancelled  = bookings.stream().filter(b -> b.getStatus() == Booking.Status.CANCELLED).count();
        double revenue  = bookings.stream()
                .filter(b -> b.getStatus() == Booking.Status.CONFIRMED)
                .mapToDouble(Booking::getTotalPrice)
                .sum();

        model.addAttribute("bookings",   bookings);
        model.addAttribute("rooms",      rooms);
        model.addAttribute("confirmed",  confirmed);
        model.addAttribute("pending",    pending);
        model.addAttribute("cancelled",  cancelled);
        model.addAttribute("revenue",    revenue);
        model.addAttribute("hotelName",  "The Grand Horizon");
        model.addAttribute("pageTitle",  "Admin Dashboard");
        return "admin";
    }

    // POST /admin/bookings/{id}/cancel
    @PostMapping("/bookings/{id}/cancel")
    public String cancelBooking(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        boolean cancelled = bookingService.cancelBooking(id);
        redirectAttributes.addFlashAttribute(
            cancelled ? "success" : "error",
            cancelled ? "Booking cancelled successfully." : "Booking not found."
        );
        return "redirect:/admin";
    }

    // POST /admin/rooms/{id}/toggle — toggle room availability
    @PostMapping("/rooms/{id}/toggle")
    public String toggleRoom(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        roomService.getRoomById(id).ifPresent(room -> {
            room.setAvailable(!room.isAvailable());
            roomService.saveRoom(room);
        });
        redirectAttributes.addFlashAttribute("success", "Room availability updated.");
        return "redirect:/admin";
    }
}
