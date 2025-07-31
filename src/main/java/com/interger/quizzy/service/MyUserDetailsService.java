package com.interger.quizzy.service;

import com.interger.quizzy.model.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class MyUserDetailsService implements UserDetailsService {

    private final UserService userService;

    // Constructor injection instead of field injection
    public MyUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user= null;
        if(username.contains("@")) {
             user = userService.getUserByUserName(username);
        }else{
             user = userService.getUserByUserEmail(username);
        }

        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }
        return new MyUserPrincipal(user);
    }
}