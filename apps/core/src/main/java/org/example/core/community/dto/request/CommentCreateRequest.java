package org.example.core.community.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CommentCreateRequest(
        @NotBlank
        String content,
        Long parentId
) { }
