package com.interger.quizzy.controller;


import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.interger.quizzy.config.JwtUtil;
import com.interger.quizzy.model.User;
import com.interger.quizzy.model.requests.*;
import com.interger.quizzy.model.responses.AuthResponse;
import com.interger.quizzy.service.MyUserPrincipal;
import com.interger.quizzy.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.google.api.client.json.jackson2.JacksonFactory;


import java.util.Collections;

@RestController
@RequestMapping("/api/auth/")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    @PostMapping("login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest request) {

        String userName = request.getUserName();
        String password = request.getPassword();
        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUserName(),
                            request.getPassword()
                    )
            );
            MyUserPrincipal principal = (MyUserPrincipal) authentication.getPrincipal();
            User user = principal.getUser();

            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);
            AuthResponse response = AuthResponse
                    .builder()
                    .userId(user.getUserId())
                    .message("Login Successful")
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();


            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body("Invalid username or password");
        }
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserRegisterRequest request) {
        // 1. Basic null/blank validation
        if (request.getFullName() == null || request.getFullName().isBlank()
                || request.getEmail() == null || request.getEmail().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {

            return ResponseEntity.badRequest().body(
                    AuthResponse.builder()
                            .message("Missing required fields: fullName, email, or password")
                            .build()
            );
        }

        // 2. Check if user already exists
        if (userService.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    "User already exists with email: " + request.getEmail()

            );
        }




        // 6. Save user
        User user = userService.register(request); // internally uses repository.save(user)

        // 7. Generate token
        String accessToken = jwtUtil.generateAccessToken(user);
        String refreshToken = jwtUtil.generateRefreshToken(user);

        // 8. Return 201 response
        return ResponseEntity.status(HttpStatus.CREATED).body(
                AuthResponse.builder()
                        .userId(user.getUserId())
                        .message("User registered successfully")
                        .accessToken(accessToken)
                        .refreshToken(refreshToken)
                        .build()
        );
    }

    @PostMapping("refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request){
        return ResponseEntity.ok("ok");
    }
    @PostMapping("/google-login")
    public ResponseEntity<AuthResponse> googleLogin(@RequestBody GoogleLoginRequest request) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                    // TODO access this from env variables instead
                    .setAudience(Collections.singletonList("257758548171-c1e9vie5tisj4sa3l0e9314ldpjk72f1.apps.googleusercontent.com"))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.getIdToken());
            if (idToken == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(AuthResponse.builder()
                        .message("Invalid ID token").build());
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String fullName = (String) payload.get("name");
            String avatar = (String) payload.get("picture");

            String username = email.split("@")[0];
            String oAuthId =payload.getSubject();
            String oauthProvider = "google";




            // check if user exists in DB by email
            User user = userService.findOrCreateByEmail(email,username, fullName, avatar,oAuthId,oauthProvider); // write this method in service

            // issue app-specific access token
            String accessToken = jwtUtil.generateAccessToken(user);
            String refreshToken = jwtUtil.generateRefreshToken(user);

            return ResponseEntity.ok(AuthResponse.builder()
                    .userId(user.getUserId())
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .message("Login successful")
                    .build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(AuthResponse.builder()
                    .message("Authentication failed: " + e.getMessage()).build());
        }
    }



}
