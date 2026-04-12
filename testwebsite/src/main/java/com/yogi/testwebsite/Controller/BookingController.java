package com.yogi.testwebsite.Controller;

import com.yogi.testwebsite.Entity.Booking;
import com.yogi.testwebsite.Entity.Room;
import com.yogi.testwebsite.Service.BookingService;
import com.yogi.testwebsite.Service.BookingService.RoomNotAvailableException;
import com.yogi.testwebsite.Service.RoomService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/booking")
public class BookingController {

    private final BookingService bookingService;
    private final RoomService    roomService;

    private static final DateTimeFormatter DISPLAY_FMT =
            DateTimeFormatter.ofPattern("dd MMM yyyy");

    public BookingController(BookingService bookingService, RoomService roomService) {
        this.bookingService = bookingService;
        this.roomService    = roomService;
    }

    // GET /booking
    @GetMapping
    public String bookingForm(@RequestParam(required = false) Long roomId, Model model) {
        List<Room> rooms = roomService.getAvailableRooms();
        Optional<Room> selectedRoom = roomId != null
                ? roomService.getRoomById(roomId)
                : Optional.empty();

        model.addAttribute("rooms",          rooms);
        model.addAttribute("selectedRoom",   selectedRoom.orElse(null));
        model.addAttribute("selectedRoomId", roomId);
        model.addAttribute("hotelName",      "The Grand Horizon");
        model.addAttribute("pageTitle",      "Book Your Stay");
        model.addAttribute("activePage",     "booking");

        return "booking";
    }

    // POST /booking
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

        // ── Date sanity checks ─────────────────────────────────────────
        if (checkIn.isBefore(LocalDate.now())) {
            redirectAttributes.addFlashAttribute("error",
                    "Check-in date cannot be in the past.");
            return "redirect:/booking?roomId=" + roomId;
        }

        if (!checkOut.isAfter(checkIn)) {
            redirectAttributes.addFlashAttribute("error",
                    "Check-out date must be after check-in date.");
            return "redirect:/booking?roomId=" + roomId;
        }

        // ── Attempt booking ────────────────────────────────────────────
        try {
            Booking booking = bookingService.createBooking(
                    roomId, firstName, lastName, email, phone, country,
                    checkIn, checkOut, guestCount, specialRequests);

            return "redirect:/booking/confirmation/" + booking.getConfirmationCode();

        } catch (BookingService.RoomNotAvailableException ex) {
            String msg = String.format(
                    "Sorry, %s is already booked from %s to %s. " +
                    "It is next available from %s — please choose different dates or select another room.",
                    ex.getRoomName(),
                    ex.getRequestedCheckIn().format(DISPLAY_FMT),
                    ex.getRequestedCheckOut().format(DISPLAY_FMT),
                    ex.getNextAvailableDate().format(DISPLAY_FMT)
            );
            redirectAttributes.addFlashAttribute("error", msg);
            return "redirect:/booking?roomId=" + roomId;

        } catch (Exception ex) {
            redirectAttributes.addFlashAttribute("error",
                    "Something went wrong. Please try again.");
            return "redirect:/booking?roomId=" + roomId;
        }
    }

    // GET /booking/confirmation/{code}
    @GetMapping("/confirmation/{code}")
    public String confirmation(@PathVariable String code, Model model) {
        Optional<Booking> bookingOpt = bookingService.findByConfirmationCode(code);

        if (bookingOpt.isEmpty()) {
            return "redirect:/booking";
        }

        model.addAttribute("booking",    bookingOpt.get());
        model.addAttribute("hotelName",  "The Grand Horizon");
        model.addAttribute("pageTitle",  "Booking Confirmed");
        model.addAttribute("activePage", "booking");

        return "booking-confirm";
    }

    // GET /booking/lookup
    @GetMapping("/lookup")
    public String lookupForm(Model model) {
        model.addAttribute("hotelName",  "The Grand Horizon");
        model.addAttribute("pageTitle",  "Find Your Booking");
        model.addAttribute("activePage", "booking");
        return "booking-lookup";
    }

    // POST /booking/lookup
    @PostMapping("/lookup")
    public String lookupSubmit(@RequestParam String code,
                               RedirectAttributes redirectAttributes) {
        Optional<Booking> booking = bookingService
                .findByConfirmationCode(code.trim().toUpperCase());

        if (booking.isPresent()) {
            return "redirect:/booking/confirmation/" + booking.get().getConfirmationCode();
        }

        redirectAttributes.addFlashAttribute("error",
                "No booking found with that confirmation code.");
        return "redirect:/booking/lookup";
    }

    /**
     * GET /booking/check-availability

     * AJAX endpoint called by the booking form JS when the user
     * picks dates. Returns JSON so the form can show a real-time
     * availability indicator without a full page reload.

     * Response: { "available": true }
     *        or { "available": false, "message": "..." }
     */
    @GetMapping("/check-availability")
    @ResponseBody
    public java.util.Map<String, Object> checkAvailability(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkIn,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOut) {

        var result = new java.util.LinkedHashMap<String, Object>();

        if (checkIn == null || checkOut == null || !checkOut.isAfter(checkIn)) {
            result.put("available", false);
            result.put("message", "Check-out must be after check-in.");
            return result;
        }

        // isRoomAvailable now handles passing the CANCELLED enum internally
        boolean available = bookingService.isRoomAvailable(roomId, checkIn, checkOut);
        result.put("available", available);

        if (!available) {
            result.put("message",
                    "This room is already booked for part of those dates. " +
                    "Please choose different dates or another room.");
        }

        return result;
    }
}

