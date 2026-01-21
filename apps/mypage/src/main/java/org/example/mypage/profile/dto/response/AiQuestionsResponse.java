package org.example.mypage.profile.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

public class AiQuestionsResponse {
    private List<Question> questions;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Question {
        private String title;
        private String description;
        private String question;
    }
}
