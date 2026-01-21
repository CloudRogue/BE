package org.example.mypage.profile.dto.response;


import lombok.Builder;


/**
 * 프로필 조회/응답 DTO입니다.
 *
 * <h2>용도</h2>
 * <ul>
 *   <li>마이페이지 프로필 조회 API의 응답으로 사용됩니다.</li>
 *   <li>클라이언트가 화면에 표시할 프로필 값들과 온보딩 완료 여부를 함께 제공합니다.</li>
 * </ul>
 *
 */
@Builder
public record ProfileResponse(
        String name,
        String email

) {}

