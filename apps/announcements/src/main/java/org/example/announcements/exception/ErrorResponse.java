package org.example.announcements.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private final String code; // 서버 정의 에러 코드
    private final String message; // 에러 메시지
    private final int status; // HTTP 상태 코드
    private final Object details; // 필드 에러 등 추가 정보

}
