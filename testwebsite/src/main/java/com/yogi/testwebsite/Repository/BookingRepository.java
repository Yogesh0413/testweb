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

    @Query("""
        SELECT b FROM Booking b
        WHERE b.room.id = :roomId
          AND b.status <> com.hotelresort.model.Booking$Status.CANCELLED
          AND b.checkIn  < :checkOut
          AND b.checkOut > :checkIn
    """)
    List<Booking> findConflictingBookings(
            @Param("roomId")   Long      roomId,
            @Param("checkIn") LocalDate checkIn,
            @Param("checkOut") LocalDate checkOut
    );

    /**
     * Same as above but excludes a specific booking —
     * useful when modifying an existing booking so it
     * does not conflict with itself.
     */
    @Query("""
        SELECT b FROM Booking b
        WHERE b.room.id  = :roomId
          AND b.id       <> :excludeId
          AND b.status   <> com.hotelresort.model.Booking$Status.CANCELLED
          AND b.checkIn  < :checkOut
          AND b.checkOut > :checkIn
    """)
    List<Booking> findConflictingBookingsExcluding(
            @Param("roomId")    Long      roomId,
            @Param("checkIn")   LocalDate checkIn,
            @Param("checkOut")  LocalDate checkOut,
            @Param("excludeId") Long      excludeId
    );
}
