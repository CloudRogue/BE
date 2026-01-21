package org.example.admin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AIApiRequest {
    private String link;
    private String publisher;
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
