package com.interger.quizzy.model.requests;

import lombok.Data;

@Data
public class UserRegisterRequest {
    private String fullName;
    private String email;
    private String password;
    private String avatar;
}