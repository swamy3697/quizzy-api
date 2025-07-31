package com.interger.quizzy.controller;


import com.interger.quizzy.model.requests.OauthLoginRequest;
import com.interger.quizzy.model.requests.RefreshTokenRequest;
import com.interger.quizzy.model.requests.UserLoginRequest;
import com.interger.quizzy.model.requests.UserRegisterRequest;
import com.interger.quizzy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth/")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest request){


    }

    @PostMapping("register")
    public ResponseEntity<?> login(@RequestBody UserRegisterRequest request){

    }

    @PostMapping("refresh-token")
    public ResponseEntity<?> login(@RequestBody RefreshTokenRequest request){

    }


    @PostMapping("google-auth")
    public ResponseEntity<?> login(@RequestBody OauthLoginRequest request){

    }


}
