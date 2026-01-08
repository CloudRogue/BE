package org.example.mypage.service;

import org.example.mypage.dto.request.ProfilePatchRequest;
import org.example.mypage.dto.request.ProfileUpsertRequest;


/**
 * 온보딩(프로필 입력/수정) 도메인 서비스 인터페이스입니다.
 *
 * <h2>역할</h2>
 * <ul>
 *   <li>사용자의 프로필 입력(온보딩 완료) 및 이후 프로필 변경을 처리합니다.</li>
 *   <li>{@code submitProfile}은 "전체 값" 기반의 생성/덮어쓰기 성격(UPSERT/PUT)에 맞춰 설계됩니다.</li>
 *   <li>{@code updateProfile}은 부분 변경(PATCH) 요청을 반영합니다.</li>
 * </ul>
 *
 * <h2>정책 메모</h2>
 * <ul>
 *   <li>프로필이 존재하지 않을 때 {@code submitProfile}을 POST(Create-only)로 취급할지,
 *       PUT(없으면 생성/있으면 덮어쓰기)로 취급할지는 서비스 정책에 따라 달라집니다.</li>
 * </ul>
 */
public interface OnboardingService {

    /**
     * 프로필을 제출 생성 하지만 추후 전체 덮어쓰기 가능성 있습니다.
     *
     * <p>일반적으로 온보딩 완료 시점에 사용되며, 요청 DTO의 모든 필드가 필수입니다.</p>
     *
     * @param userId 사용자 식별자(예: ULID 문자열)
     * @param upsertRequest 프로필 전체 입력/갱신 요청 DTO
     */
    void submitProfile(String userId, ProfileUpsertRequest upsertRequest);

    /**
     * 프로필을 부분 수정(PATCH)합니다.
     *
     * <p>요청 DTO에서 null이 아닌 값만 반영합니다.</p>
     *
     * @param userId 사용자 식별자(예: ULID 문자열)
     * @param patchRequest 프로필 부분 수정 요청 DTO
     */
    void updateProfile(String userId, ProfilePatchRequest patchRequest);
}
