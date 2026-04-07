package com.yogi.testwebsite.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AmenitiesController {

    @GetMapping("/amenities")
    public String amenities(Model model) {
        model.addAttribute("hotelName",  "The Grand Horizon");
        model.addAttribute("pageTitle",  "Amenities");
        model.addAttribute("activePage", "amenities");
        return "amenities";
    }
}
