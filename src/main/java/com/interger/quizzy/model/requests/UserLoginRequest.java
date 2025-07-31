package com.interger.quizzy.model.requests;

import lombok.Data;

@Data
public class UserLoginRequest {
    String userName;
    String password;
}
