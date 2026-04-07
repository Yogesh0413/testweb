package com.yogi.testwebsite.Controller;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private static final String HOTEL_NAME = "The Grand Horizon";

    // GET / — homepage
    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("hotelName", HOTEL_NAME);
        model.addAttribute("tagline",   "Where Luxury Meets Serenity");
        model.addAttribute("activePage", "home");
        return "index";
    }

    // GET /contact — contact page
    @GetMapping("/contact")
    public String contact(Model model) {
        model.addAttribute("hotelName",  HOTEL_NAME);
        model.addAttribute("pageTitle",  "Contact Us");
        model.addAttribute("activePage", "contact");
        return "contact";
    }
}