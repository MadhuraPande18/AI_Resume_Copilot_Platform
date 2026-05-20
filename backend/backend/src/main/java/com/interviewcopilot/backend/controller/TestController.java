package com.interviewcopilot.backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class TestController {

    @GetMapping("/")
    public String home() {

        return "AI Interview Copilot Backend Running Successfully 🚀";
    }
}