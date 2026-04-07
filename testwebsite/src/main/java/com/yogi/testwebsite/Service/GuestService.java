package com.yogi.testwebsite.Service;

import com.yogi.testwebsite.Entity.Guest;
import com.yogi.testwebsite.Repository.GuestRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GuestService {

    private final GuestRepository guestRepository;

    public GuestService(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }

    // Find existing guest by email or create a new one
    public Guest findOrCreate(String firstName, String lastName,
                               String email, String phone, String country) {
        Optional<Guest> existing = guestRepository.findByEmail(email);
        if (existing.isPresent()) {
            Guest g = existing.get();
            g.setFirstName(firstName);
            g.setLastName(lastName);
            g.setPhone(phone);
            g.setCountry(country);
            return guestRepository.save(g);
        }
        return guestRepository.save(new Guest(firstName, lastName, email, phone, country));
    }

    public Optional<Guest> findByEmail(String email) {
        return guestRepository.findByEmail(email);
    }
}
