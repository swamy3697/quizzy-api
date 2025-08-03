package com.interger.quizzy.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/tags")
public class TagController {

    @GetMapping()
    public ResponseEntity<?> getTags(){
        return ResponseEntity.ok("ok");
    }

    // get meta data of tags
    // or leftover tags

}
