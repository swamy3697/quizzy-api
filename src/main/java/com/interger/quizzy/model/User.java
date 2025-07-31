package com.interger.quizzy.model;

import lombok.Data;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    private Long userId;

    private String fullName;

    private String email;

    private String userName;

    private String oauthProvider;

    private String oauthId;

    private String passwordHash;

    private Map<String, Object> socialLinks;

    private String avatar;

    private Map<String, Object> solvedTags;

    private Long questionsCreated;

    private Long questionsAnswered;

    private LocalDateTime createdAt;
}
