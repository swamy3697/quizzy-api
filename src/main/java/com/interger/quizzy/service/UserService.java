package com.interger.quizzy.service;

import com.interger.quizzy.model.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public User getUserByUserEmail(String username) {
        // send this to repo and get the user
        return new User();
    }

    public User getUserByUserName(String username) {
        return new User();
    }
}
