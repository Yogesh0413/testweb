package com.yogi.testwebsite.Controller;

import com.yogi.testwebsite.Entity.Booking;
import com.yogi.testwebsite.Entity.Room;
import com.yogi.testwebsite.Service.BookingService;
import com.yogi.testwebsite.Service.RoomService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/booking")
public class BookingController {

    private final BookingService bookingService;
    private final RoomService roomService;

    public BookingController(BookingService bookingService, RoomService roomService) {
        this.bookingService = bookingService;
        this.roomService    = roomService;
    }

    // GET /booking — show booking form, optionally pre-select a room
    @GetMapping
    public String bookingForm(@RequestParam(required = false) Long roomId, Model model) {
        List<Room> rooms = roomService.getAvailableRooms();
        Optional<Room> selectedRoom = roomId != null
                ? roomService.getRoomById(roomId)
                : Optional.empty();

        model.addAttribute("rooms", rooms);
        model.addAttribute("selectedRoom", selectedRoom.orElse(null));
        model.addAttribute("selectedRoomId", roomId);
        model.addAttribute("hotelName", "The Grand Horizon");
        model.addAttribute("pageTitle", "Book Your Stay");
        model.addAttribute("activePage", "booking");

        return "booking";
    }

    // POST /booking — process the booking form submission
    @PostMapping
    public String submitBooking(
            @RequestParam Long roomId,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String phone,
            @RequestParam String country,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut,
            @RequestParam int guestCount,
            @RequestParam(required = false) String specialRequests,
            RedirectAttributes redirectAttributes) {

        // Basic validation
        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            redirectAttributes.addFlashAttribute("error", "Check-out date must be after check-in date.");
            return "redirect:/booking?roomId=" + roomId;
        }

        try {
            Booking booking = bookingService.createBooking(
                    roomId, firstName, lastName, email, phone, country,
                    checkIn, checkOut, guestCount, specialRequests);

            return "redirect:/booking/confirmation/" + booking.getConfirmationCode();

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Something went wrong. Please try again.");
            return "redirect:/booking?roomId=" + roomId;
        }
    }

    // GET /booking/confirmation/{code} — show confirmation page
    @GetMapping("/confirmation/{code}")
    public String confirmation(@PathVariable String code, Model model) {
        Optional<Booking> bookingOpt = bookingService.findByConfirmationCode(code);

        if (bookingOpt.isEmpty()) {
            return "redirect:/booking";
        }

        model.addAttribute("booking", bookingOpt.get());
        model.addAttribute("hotelName", "The Grand Horizon");
        model.addAttribute("pageTitle", "Booking Confirmed");
        model.addAttribute("activePage", "booking");

        return "booking-confirm";
    }

    // GET /booking/lookup — look up a booking by confirmation code
    @GetMapping("/lookup")
    public String lookupForm(Model model) {
        model.addAttribute("hotelName", "The Grand Horizon");
        model.addAttribute("pageTitle", "Find Your Booking");
        model.addAttribute("activePage", "booking");
        return "booking-lookup";
    }

    @PostMapping("/lookup")
    public String lookupSubmit(@RequestParam String code, RedirectAttributes redirectAttributes) {
        Optional<Booking> booking = bookingService.findByConfirmationCode(code.trim().toUpperCase());
        if (booking.isPresent()) {
            return "redirect:/booking/confirmation/" + booking.get().getConfirmationCode();
        }
        redirectAttributes.addFlashAttribute("error", "No booking found with that code.");
        return "redirect:/booking/lookup";
    }
}
