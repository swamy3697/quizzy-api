package com.interger.quizzy.controller;

import com.interger.quizzy.model.OptionDTO;
import com.interger.quizzy.model.requests.QuestionCreateRequest;
import com.interger.quizzy.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@RestController
@RequestMapping("/api/quiz/")
public class QuizController {
    // creating questions
    // answering
    // get random questions
    // get filters questions
    // create bundle of questions
    // respect a question
    // get questions created by me // future

    private final QuizService quizService;



    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody QuestionCreateRequest request) {
        try {


            // Validate required fields
            if (request.getQuestion() == null || request.getQuestion().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Question is required"));
            }

            if (request.getOptions() == null || request.getOptions().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "At least one option is required"));
            }

            if (request.getTags() == null || request.getTags().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "At least one tag is required"));
            }

            List<String> validLevels = List.of("easy", "medium", "hard", "god level");
            if (request.getLevel() == null || !validLevels.contains(request.getLevel().toLowerCase())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Valid difficulty level is required"));
            }

            List<String> validTypes = List.of("single", "multi-choice");
            if (request.getType() == null || !validTypes.contains(request.getType().toLowerCase())) {
                return ResponseEntity.badRequest().body(Map.of("error", "Valid question type is required"));
            }

//            for (int i = 0; i < request.getOptions().size(); i++) {
//                var option = request.getOptions().get(i);
//                if (option.getOption() == null || option.getOption().trim().isEmpty()) {
//                    return ResponseEntity.badRequest().body(Map.of("error", "Option " + (i + 1) + " text is required"));
//                }
//                // Boolean check not needed in Java, if field is primitive boolean.
//            }

            // At least one correct option for multi-choice
//            if (request.getType().equalsIgnoreCase("multi-choice") &&
//                    request.getOptions().stream().noneMatch(OptionDTO::isCorrect)) {
//                return ResponseEntity.badRequest().body(Map.of("error", "At least one option must be marked as correct"));
//            }
            boolean successful =quizService.create(1, request);

             if(successful){
                 return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Question created successfully"));
             }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error","something happened"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", ex.getMessage()));
        }
    }



}
