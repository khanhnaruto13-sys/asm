package com.example.asm1.controller;

import com.example.asm1.entity.User;
import com.example.asm1.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    // ===== LOGIN FORM =====
    @GetMapping("/login")
    public String loginForm() {
        return "auth/login";
    }

    // ===== LOGIN THƯỜNG =====
    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            Model model
    ) {
        try {
            User user = authService.login(email, password);

            session.setAttribute("user", user);
            session.setAttribute("cartCount", authService.getCartCount(user));

            // Quay lại URL bảo mật nếu có
            String securityUri = (String) session.getAttribute("securityUri");
            if (securityUri != null) {
                session.removeAttribute("securityUri");
                return "redirect:" + securityUri;
            }

            if (user.isAdmin()) {
                return "redirect:/products/crud";
            }

            return "redirect:/";

        } catch (Exception e) {
            model.addAttribute("error", "Sai tài khoản hoặc mật khẩu");
            return "auth/login";
        }
    }

    // ===== REGISTER =====
    @PostMapping("/register")
    public String register(
            @RequestParam String fullname,
            @RequestParam String email,
            @RequestParam String password,
            Model model
    ) {
        try {
            authService.register(fullname, email, password);
            return "redirect:/auth/login";
        } catch (Exception e) {
            model.addAttribute("error", "Email đã tồn tại");
            return "auth/register";
        }
    }

    // ===== GOOGLE LOGIN =====
    @GetMapping("/google")
    public String googleLogin() {

        String url = "https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + clientId
                + "&redirect_uri=" + redirectUri
                + "&response_type=code"
                + "&scope=email%20profile";

        return "redirect:" + url;
    }

    // ===== GOOGLE CALLBACK =====
    @GetMapping("/google/callback")
    public String googleCallback(
            @RequestParam("code") String code,
            HttpSession session
    ) {

        RestTemplate restTemplate = new RestTemplate();

        String tokenUrl = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        Map tokenResponse = restTemplate.postForObject(tokenUrl, params, Map.class);

        String accessToken = (String) tokenResponse.get("access_token");

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v2/userinfo",
                HttpMethod.GET,
                entity,
                Map.class
        );

        Map userInfo = response.getBody();

        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");

        User user = authService.findOrCreateGoogleUser(name, email);

        session.setAttribute("user", user);
        session.setAttribute("cartCount", authService.getCartCount(user));

        return "redirect:/";
    }

    // ===== LOGOUT =====
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}