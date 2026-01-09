package org.example.core.community.dto.response;

import java.util.List;

public record CommentSliceResponse(
        List<CommentContentResponse> contents,

        Long nextCursor,

        boolean hasNext
) {}