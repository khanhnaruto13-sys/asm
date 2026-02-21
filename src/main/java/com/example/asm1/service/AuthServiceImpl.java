package com.example.asm1.service;

import com.example.asm1.entity.User;
import com.example.asm1.repository.CartItemRepository;
import com.example.asm1.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Override
    public User login(String email, String password) {

        if (email == null || email.isEmpty() ||
                password == null || password.isEmpty()) {
            throw new RuntimeException("EMPTY");
        }

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new RuntimeException("NOT_FOUND");
        }

        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("WRONG_PASSWORD");
        }

        return user;
    }

    @Override
    public void register(String fullname, String email, String password) {

        if (userRepository.findByEmail(email) != null) {
            throw new RuntimeException("EMAIL_EXIST");
        }

        User user = new User();
        user.setFullname(fullname);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole("USER");

        userRepository.save(user);
    }

    @Override
    public int getCartCount(User user) {
        return cartItemRepository.findByUser(user).size();
    }

    @Override
    public User findOrCreateGoogleUser(String fullname, String email) {

        User user = userRepository.findByEmail(email);

        if (user == null) {
            user = new User();
            user.setFullname(fullname);
            user.setEmail(email);
            user.setPassword("");
            user.setRole("USER");
            userRepository.save(user);
        }

        return user;
    }
}