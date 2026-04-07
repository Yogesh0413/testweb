package com.yogi.testwebsite.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;         // e.g. "Deluxe", "Suite", "Villa"
    private String description;
    private double pricePerNight;
    private int maxGuests;
    private int bedCount;
    private String bedType;      // e.g. "King", "Twin", "Double"
    private double sizeSqm;
    private boolean available;
    private String imageUrl;
    private String view;         // e.g. "Ocean", "Garden", "Pool"

    // --- Constructors ---
    public Room() {}

    public Room(String name, String type, String description, double pricePerNight,
                int maxGuests, int bedCount, String bedType, double sizeSqm,
                boolean available, String imageUrl, String view) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.pricePerNight = pricePerNight;
        this.maxGuests = maxGuests;
        this.bedCount = bedCount;
        this.bedType = bedType;
        this.sizeSqm = sizeSqm;
        this.available = available;
        this.imageUrl = imageUrl;
        this.view = view;
    }

    // --- Getters & Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public double getPricePerNight() { return pricePerNight; }
    public void setPricePerNight(double pricePerNight) { this.pricePerNight = pricePerNight; }

    public int getMaxGuests() { return maxGuests; }
    public void setMaxGuests(int maxGuests) { this.maxGuests = maxGuests; }

    public int getBedCount() { return bedCount; }
    public void setBedCount(int bedCount) { this.bedCount = bedCount; }

    public String getBedType() { return bedType; }
    public void setBedType(String bedType) { this.bedType = bedType; }

    public double getSizeSqm() { return sizeSqm; }
    public void setSizeSqm(double sizeSqm) { this.sizeSqm = sizeSqm; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getView() { return view; }
    public void setView(String view) { this.view = view; }
}
