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
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        User user;

        if (identifier.contains("@")) {
            user = userService.getUserByUserEmail(identifier);
        } else {
            user = userService.getUserByUserName(identifier);
        }

        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + identifier);
        }

        return new MyUserPrincipal(user);
    }

    // ✅ Used explicitly in JwtAuthFilter for user_id-based lookup
    public UserDetails loadUserByUserId(Long userId) {
        return userService.getUserById(userId)
                .map(MyUserPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with ID: " + userId));
    }
}