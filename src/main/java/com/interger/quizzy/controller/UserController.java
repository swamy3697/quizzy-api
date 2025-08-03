package com.interger.quizzy.controller;


import com.interger.quizzy.model.User;
import com.interger.quizzy.model.requests.UpdateUserRequest;
import com.interger.quizzy.model.responses.LeaderboardResponse;
import com.interger.quizzy.model.responses.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

//    private Long getCurrentUserId(){
//        // get the current user id from authentication manager
//    }
//    @GetMapping("/me")
//    public ResponseEntity<UserResponse> getMyProfile() {
//        // ...
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
//        // ...
//    }
//
//    @GetMapping("/leaderboard")
//    public ResponseEntity<LeaderboardResponse> getLeaderboard() {
//        // ...
//    }
//
//    @PutMapping("/me")
//    public ResponseEntity<?> updateMyProfile(@RequestBody UpdateUserRequest request) {
//
//        // ...
//        // send 204 no content frontend will hnadle this
//    }
//
//    @DeleteMapping("/me")
//    public ResponseEntity<?> deleteMyAccount() {
//        // ...
//
//    }
}

