package com.yogi.testwebsite.Service;

import com.yogi.testwebsite.Entity.Booking;
import com.yogi.testwebsite.Entity.Guest;
import com.yogi.testwebsite.Entity.Room;
import com.yogi.testwebsite.Repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final GuestService guestService;
    private final RoomService roomService;

    public BookingService(BookingRepository bookingRepository,
                          GuestService guestService,
                          RoomService roomService) {
        this.bookingRepository = bookingRepository;
        this.guestService      = guestService;
        this.roomService       = roomService;
    }

    /**
     * Returns true if the room is available for the requested dates —
     * i.e. no confirmed or pending bookings overlap with the range.
     */
    public boolean isRoomAvailable(Long roomId, LocalDate checkIn, LocalDate checkOut) {
        return bookingRepository
                .findConflictingBookings(roomId, checkIn, checkOut)
                .isEmpty();
    }

    /**
     * Same as above but ignores a specific booking ID (for future
     * modification flows so a booking doesn't block itself).
     */
    public boolean isRoomAvailableExcluding(Long roomId, LocalDate checkIn,
                                            LocalDate checkOut, Long excludeBookingId) {
        return bookingRepository
                .findConflictingBookingsExcluding(roomId, checkIn, checkOut, excludeBookingId)
                .isEmpty();
    }

    /**
     * Creates a booking after checking for date conflicts.
     * Throws IllegalStateException if the room is not available.
     */
    public Booking createBooking(Long roomId,
                                 String firstName, String lastName,
                                 String email, String phone, String country,
                                 LocalDate checkIn, LocalDate checkOut,
                                 int guestCount, String specialRequests) {

        Room room = roomService.getRoomById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));

        // ── Conflict check ──────────────────────────────────────────────
        if (!isRoomAvailable(roomId, checkIn, checkOut)) {
            List<Booking> conflicts = bookingRepository
                    .findConflictingBookings(roomId, checkIn, checkOut);

            // Find the latest check-out among conflicts so we can
            // suggest the next available date to the guest
            LocalDate nextAvailable = conflicts.stream()
                    .map(Booking::getCheckOut)
                    .max(LocalDate::compareTo)
                    .orElse(checkOut.plusDays(1));

            throw new RoomNotAvailableException(
                    room.getName(), checkIn, checkOut, nextAvailable
            );
        }
        // ───────────────────────────────────────────────────────────────

        Guest guest = guestService.findOrCreate(firstName, lastName, email, phone, country);

        Booking booking = new Booking();
        booking.setRoom(room);
        booking.setGuest(guest);
        booking.setCheckIn(checkIn);
        booking.setCheckOut(checkOut);
        booking.setGuestCount(guestCount);
        booking.setSpecialRequests(specialRequests);
        booking.setConfirmationCode(generateCode());
        booking.setStatus(Booking.Status.CONFIRMED);
        booking.calculateTotalPrice();

        return bookingRepository.save(booking);
    }

    public Optional<Booking> findByConfirmationCode(String code) {
        return bookingRepository.findByConfirmationCode(code);
    }

    public List<Booking> findByGuestEmail(String email) {
        return bookingRepository.findByGuest_Email(email);
    }

    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public boolean cancelBooking(Long id) {
        Optional<Booking> opt = bookingRepository.findById(id);
        if (opt.isPresent()) {
            Booking b = opt.get();
            b.setStatus(Booking.Status.CANCELLED);
            bookingRepository.save(b);
            return true;
        }
        return false;
    }

    private String generateCode() {
        return "GH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    // ── Custom exception ─────────────────────────────────────────────────

    public static class RoomNotAvailableException extends RuntimeException {

        private final String    roomName;
        private final LocalDate requestedCheckIn;
        private final LocalDate requestedCheckOut;
        private final LocalDate nextAvailableDate;

        public RoomNotAvailableException(String roomName, LocalDate checkIn,
                                         LocalDate checkOut, LocalDate nextAvailable) {
            super("Room '" + roomName + "' is not available from " + checkIn + " to " + checkOut);
            this.roomName          = roomName;
            this.requestedCheckIn  = checkIn;
            this.requestedCheckOut = checkOut;
            this.nextAvailableDate = nextAvailable;
        }

        public String    getRoomName()          { return roomName; }
        public LocalDate getRequestedCheckIn()  { return requestedCheckIn; }
        public LocalDate getRequestedCheckOut() { return requestedCheckOut; }
        public LocalDate getNextAvailableDate() { return nextAvailableDate; }
    }
}
