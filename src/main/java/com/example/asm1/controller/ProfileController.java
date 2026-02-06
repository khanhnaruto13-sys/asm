package com.example.asm1.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    @GetMapping("")
    public String profile() {
        return "profile/profile";
    }
//    @GetMapping("")
//    public String profile(HttpSession session) {
//        if (session.getAttribute("user") == null) {
//            return "redirect:/auth/login";
//        }
//        return "profile/profile";
//    }

}
