package org.example.core.community.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "댓글 페이징 응답")
public record CommentSliceResponse(
        @Schema(description = "댓글 목록")
        List<CommentListResponse> contents,

        @Schema(description = "다음 조회를 위한 커서값 (null이면 다음 페이지 없음)", example = "124")
        Long nextCursor,

        @Schema(description = "다음 페이지 존재 여부")
        boolean hasNext
) {}