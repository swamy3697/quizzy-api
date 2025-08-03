package com.interger.quizzy.service;

import com.interger.quizzy.config.PasswordEncoderConfig;
import com.interger.quizzy.model.User;
import com.interger.quizzy.model.requests.UserRegisterRequest;
import com.interger.quizzy.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {

    private final UserRepository userRepo;

    private final PasswordEncoder passwordEncoder;
    public User getUserByUserEmail(String email) {
        // send this to repo and get the user
        return userRepo.getUserByUserEmail(email);
    }

    public User getUserByUserName(String username) {
        return userRepo.getUserByUserName(username);
    }

    public boolean existsByEmail(String email) {


        return userRepo.existsByEmail(email);
    }

    public User register(UserRegisterRequest request) {
        String username = request.getEmail().split("@")[0];
        String hashedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .userName(username)
                .passwordHash(hashedPassword)
                .avatar(request.getAvatar())
                .build();

        Long user_id=userRepo.save(user);
        user.setUserId(user_id);

        return user;
    }

    public User findOrCreateByEmail(String email, String username, String fullName, String avatar, String oauthId, String oauthProvider) {
        Optional<User> existing = userRepo.findByEmail(email);
        if (existing.isPresent()) {
            userRepo.updateAvatar(email, avatar);
            existing.get().setAvatar(avatar);
            return existing.get();
        }

        return userRepo.insertOAuthUser(email, username, fullName, avatar, oauthId, oauthProvider);
    }

    public Optional<User> getUserById(Long userId) {
        return userRepo.getUserByUserId(userId);
    }
}
