package com.yogi.testwebsite.Repository;

import com.yogi.testwebsite.Entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByConfirmationCode(String confirmationCode);
    List<Booking> findByGuest_Email(String email);
    List<Booking> findByRoom_Id(Long roomId);

    /**
     * Finds any active (non-cancelled) bookings for a room
     * that overlap with the requested date range.
     *
     * Overlap condition:
     *   existing.checkIn  < requested.checkOut
     *   AND
     *   existing.checkOut > requested.checkIn
     */
    @Query(value = "SELECT b FROM Booking b " +
                   "WHERE b.room.id = :roomId " +
                   "AND b.status <> :cancelled " +
                   "AND b.checkIn < :checkOut " +
                   "AND b.checkOut > :checkIn")
    List<Booking> findConflictingBookings(
            @Param("roomId")    Long      roomId,
            @Param("checkIn")   LocalDate checkIn,
            @Param("checkOut")  LocalDate checkOut,
            @Param("cancelled") Booking.Status cancelled
    );

    /**
     * Same as above but excludes a specific booking ID —
     * used when modifying an existing booking so it doesn't
     * conflict with itself.
     */
    @Query(value = "SELECT b FROM Booking b " +
                   "WHERE b.room.id = :roomId " +
                   "AND b.id <> :excludeId " +
                   "AND b.status <> :cancelled " +
                   "AND b.checkIn < :checkOut " +
                   "AND b.checkOut > :checkIn")
    List<Booking> findConflictingBookingsExcluding(
            @Param("roomId")    Long      roomId,
            @Param("checkIn")   LocalDate checkIn,
            @Param("checkOut")  LocalDate checkOut,
            @Param("excludeId") Long      excludeId,
            @Param("cancelled") Booking.Status cancelled
    );
}
