package org.example.mypage.profile.service;

import org.example.mypage.profile.dto.response.ProfileResponse;


/**
 * 마이페이지 도메인 서비스 인터페이스입니다.
 *
 * <h2>역할</h2>
 * <ul>
 *   <li>마이페이지 영역에서 필요한 사용자 정보를 조회/가공하여 응답 DTO로 제공합니다.</li>
 *   <li>컨트롤러는 이 인터페이스에 의존하고, 구현체에서 리포지토리/도메인 규칙을 적용합니다.</li>
 * </ul>
 */
public interface MyPageService {

    /**
     * 사용자 프로필을 조회합니다.
     *
     * @param userId 사용자 식별자(예: ULID 문자열)
     * @return 프로필 조회 응답 DTO
     */
    ProfileResponse getProfile(String userId);
    void createProfile(String userId, String email, String nickname);
}
