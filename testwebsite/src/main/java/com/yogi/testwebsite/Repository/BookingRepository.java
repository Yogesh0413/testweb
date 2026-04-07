package com.yogi.testwebsite.Repository;

import com.yogi.testwebsite.Entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Optional<Booking> findByConfirmationCode(String confirmationCode);
    List<Booking> findByGuest_Email(String email);
    List<Booking> findByRoom_Id(Long roomId);
}
