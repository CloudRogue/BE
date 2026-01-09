package org.example.core.community.dto.response;

import java.util.List;

public record CommentSliceResponse(
        List<CommentListResponse> contents,

        Long nextCursor,

        boolean hasNext
) {}