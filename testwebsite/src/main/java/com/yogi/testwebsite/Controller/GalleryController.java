package com.yogi.testwebsite.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class GalleryController {

    // Simple record to hold image data (no DB needed for a gallery)
    public record GalleryImage(String url, String caption, String category) {}

    @GetMapping("/gallery")
    public String gallery(Model model) {

        List<GalleryImage> images = List.of(
            // Rooms
            new GalleryImage("https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=800&q=80", "Deluxe Garden Room", "Rooms"),
            new GalleryImage("https://images.unsplash.com/photo-1582719478250-c89cae4dc85b?w=800&q=80", "Ocean View Suite", "Rooms"),
            new GalleryImage("https://images.unsplash.com/photo-1520250497591-112f2f40a3f4?w=800&q=80", "Grand Pool Villa", "Rooms"),
            new GalleryImage("https://images.unsplash.com/photo-1560185007-c5ca9d2c014d?w=800&q=80", "Horizon Penthouse", "Rooms"),
            new GalleryImage("https://images.unsplash.com/photo-1578683010236-d716f9a3f461?w=800&q=80", "Sunset Terrace Suite", "Rooms"),
            new GalleryImage("https://images.unsplash.com/photo-1596394516093-501ba68a0ba6?w=800&q=80", "Classic Double Room", "Rooms"),

            // Dining
            new GalleryImage("https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=800&q=80", "The Grand Restaurant", "Dining"),
            new GalleryImage("https://images.unsplash.com/photo-1424847651672-bf20a4b0982b?w=800&q=80", "Chef's Tasting Menu", "Dining"),
            new GalleryImage("https://images.unsplash.com/photo-1600565193348-f74bd3c7ccdf?w=800&q=80", "Terrace Dining at Sunset", "Dining"),
            new GalleryImage("https://images.unsplash.com/photo-1559329007-40df8a9345d8?w=800&q=80", "The Horizon Bar", "Dining"),

            // Spa & Wellness
            new GalleryImage("https://images.unsplash.com/photo-1540555700478-4be289fbecef?w=800&q=80", "Serenity Spa", "Wellness"),
            new GalleryImage("https://images.unsplash.com/photo-1515377905703-c4788e51af15?w=800&q=80", "Couples Treatment Room", "Wellness"),
            new GalleryImage("https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=800&q=80", "Thermal Pool", "Wellness"),

            // Grounds & Pool
            new GalleryImage("https://images.unsplash.com/photo-1571896349842-33c89424de2d?w=800&q=80", "Infinity Pool", "Grounds"),
            new GalleryImage("https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800&q=80", "Resort Exterior", "Grounds"),
            new GalleryImage("https://images.unsplash.com/photo-1542314831-068cd1dbfeeb?w=800&q=80", "Garden Grounds", "Grounds"),
            new GalleryImage("https://images.unsplash.com/photo-1551882547-ff40c63fe5fa?w=800&q=80", "The Grand Lobby", "Grounds")
        );

        List<String> categories = images.stream()
                .map(GalleryImage::category)
                .distinct()
                .toList();

        model.addAttribute("images", images);
        model.addAttribute("categories", categories);
        model.addAttribute("hotelName", "The Grand Horizon");
        model.addAttribute("pageTitle", "Gallery");
        model.addAttribute("activePage", "gallery");

        return "gallery";
    }
}
