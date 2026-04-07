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

    public Booking createBooking(Long roomId,
                                  String firstName, String lastName,
                                  String email, String phone, String country,
                                  LocalDate checkIn, LocalDate checkOut,
                                  int guestCount, String specialRequests) {

        Room room = roomService.getRoomById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found: " + roomId));

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
}
