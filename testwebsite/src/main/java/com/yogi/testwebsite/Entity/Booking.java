package com.yogi.testwebsite.Entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "bookings")
public class Booking {

    public enum Status { PENDING, CONFIRMED, CANCELLED }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String confirmationCode;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "room_id")
    private Room room;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "guest_id")
    private Guest guest;

    private LocalDate checkIn;
    private LocalDate checkOut;
    private int guestCount;
    private String specialRequests;

    @Enumerated(EnumType.STRING)
    private Status status = Status.PENDING;

    private double totalPrice;

    // --- Constructors ---
    public Booking() {}

    // --- Business Logic ---
    public long getNights() {
        if (checkIn == null || checkOut == null) return 0;
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    public void calculateTotalPrice() {
        if (room != null) {
            this.totalPrice = room.getPricePerNight() * getNights();
        }
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getConfirmationCode() { return confirmationCode; }
    public void setConfirmationCode(String confirmationCode) { this.confirmationCode = confirmationCode; }

    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }

    public Guest getGuest() { return guest; }
    public void setGuest(Guest guest) { this.guest = guest; }

    public LocalDate getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }

    public LocalDate getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }

    public int getGuestCount() { return guestCount; }
    public void setGuestCount(int guestCount) { this.guestCount = guestCount; }

    public String getSpecialRequests() { return specialRequests; }
    public void setSpecialRequests(String specialRequests) { this.specialRequests = specialRequests; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
}
