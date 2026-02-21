package com.example.asm1.interceptor;

import com.example.asm1.entity.User;
import jakarta.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler) throws Exception {

        String uri = request.getRequestURI();

        HttpSession session = request.getSession();
        session.setAttribute("securityUri", uri);

        User user = (User) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("/auth/login");
            return false;
        }

        if (uri.startsWith("/admin") && !user.isAdmin()) {
            response.sendRedirect("/auth/login");
            return false;
        }

        return true;
    }
}