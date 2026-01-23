package org.example.admin.dto.response;


import java.util.List;


public record AiQuestionsResponse(
        List<Question> questions
) {

    public record Question(
            String title,
            String description,
            String question
    ) {}
}
