package org.example.mypage.service;

import lombok.RequiredArgsConstructor;
import org.example.mypage.domain.Profile;
import org.example.mypage.dto.request.ProfilePatchRequest;
import org.example.mypage.dto.request.ProfileUpsertRequest;
import org.example.mypage.dto.response.ProfileResponse;
import org.example.mypage.exception.OnboardingIncompleteException;
import org.example.mypage.repository.MyPageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * 마이페이지/온보딩 프로필 기능을 제공하는 서비스 구현체입니다.
 *
 * <h2>구성</h2>
 * <ul>
 *   <li>{@link MyPageService}: 프로필 조회</li>
 *   <li>{@link OnboardingService}: 프로필 제출(생성/전체 갱신), 프로필 부분 수정</li>
 * </ul>
 *
 * <h2>예외 정책</h2>
 * <ul>
 *   <li>프로필이 존재하지 않으면 {@link OnboardingIncompleteException}을 던집니다.</li>
 *   <li>서비스 정책에 따라 "조회 시 프로필 없음"을 404/403 등으로 매핑할 수 있습니다.</li>
 * </ul>
 *
 */
@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService, OnboardingService{
    private final MyPageRepository myPageRepository;

    /**
     * 사용자 프로필을 조회합니다.
     *
     * <p>프로필이 존재하지 않으면 온보딩 미완료로 간주하여 예외를 발생시킵니다.</p>
     *
     * @param userId 사용자 식별자(예: ULID 문자열)
     * @return 프로필 조회 응답 DTO
     * @throws OnboardingIncompleteException 프로필이 존재하지 않는 경우
     */
    @Transactional(readOnly = true)
    public ProfileResponse getProfile(String userId) {
        Profile p = myPageRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(OnboardingIncompleteException::new);

        return ProfileResponse.builder()
                .name(p.getName())
                .gender(p.getGender())
                .birthDate(p.getBirthDate())
                .regionSigungu(p.getRegionSigungu())
                .householdSize(p.getHouseholdSize())
                .incomeDecile(p.getIncomeDecile())
                .householdRole(p.getHouseholdRole())
                .onboardingCompleted(true)
                .build();
    }

    /**
     * 프로필을 제출(생성/전체 갱신)합니다.
     *
     * <p>현재 구현은 프로필이 없으면 생성하고, 있으면 기존 엔티티를 그대로 저장합니다.
     * 즉, "업서트/전체 덮어쓰기"가 아니라 "create-if-absent"에 가깝습니다.</p>
     *
     * @param userId 사용자 식별자(예: ULID 문자열)
     * @param req 프로필 전체 입력 요청 DTO
     */
    @Override
    @Transactional
    public void submitProfile(String userId, ProfileUpsertRequest req) {
        Profile profile = myPageRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseGet(() -> new Profile(
                        userId,
                        req.name(),
                        req.gender(),
                        req.regionSigungu(),
                        req.incomeDecile(),
                        req.householdSize(),
                        req.birthDate(),
                        req.householdRole()
                ));

        myPageRepository.save(profile);
    }



    @Override
    @Transactional
    public void updateProfile(String userId, ProfilePatchRequest req) {
        Profile p = myPageRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(OnboardingIncompleteException::new);
        p.patch(req);

        myPageRepository.save(p);
    }

}
