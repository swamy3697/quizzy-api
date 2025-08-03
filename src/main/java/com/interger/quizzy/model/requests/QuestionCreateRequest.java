package com.interger.quizzy.model.requests;

import com.interger.quizzy.model.OptionDTO;
import lombok.Data;

import java.util.List;

@Data
public class QuestionCreateRequest {
    private String question;
    private String referTo;        // Optional
    private String description;    // Optional
    private String level;          // Enum: easy, medium, hard, god level
    private String type;           // Enum: single, multi-choice

    private List<OptionDTO> options; // Nested list of options
    private List<String> tags;

}

