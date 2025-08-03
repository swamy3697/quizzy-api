package com.interger.quizzy.service;

import com.interger.quizzy.model.requests.QuestionCreateRequest;
import com.interger.quizzy.repository.QuizRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuizService {
    private final QuizRepository quizRepo;

    public boolean create(int creatorId ,QuestionCreateRequest questionCreateRequest){
        return quizRepo.save(creatorId, questionCreateRequest);
    }
}
