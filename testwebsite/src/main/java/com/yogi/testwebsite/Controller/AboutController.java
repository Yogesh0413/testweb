package com.yogi.testwebsite.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AboutController {

    @GetMapping("/about")
    public String about(Model model) {
        model.addAttribute("hotelName", "The Grand Horizon");
        model.addAttribute("pageTitle", "Our Story");
        model.addAttribute("activePage", "about");
        return "about";
    }
}
