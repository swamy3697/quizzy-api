package com.interger.quizzy.service;

import com.interger.quizzy.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MyUserDetailsService implements UserDetailsService {

    private final UserService userService;

    // Constructor injection instead of field injection
    public MyUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String userIdStr) throws UsernameNotFoundException {
        Long userId;
        try {
            userId = Long.parseLong(userIdStr);
        } catch (NumberFormatException e) {
            throw new UsernameNotFoundException("Invalid user ID: " + userIdStr);
        }

        Optional<User> useropt = userService.getUserById(userId); // Add this method in UserService
        if (useropt.isEmpty()) {
            throw new UsernameNotFoundException("User not found with ID: " + userIdStr);
        }

        return new MyUserPrincipal(useropt.get());
    }

}