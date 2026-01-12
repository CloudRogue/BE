package org.example.mypage.service;

import lombok.RequiredArgsConstructor;
import org.example.mypage.domain.Profile;
import org.example.mypage.dto.request.AdditionalOnboardingAnswerRequest;
import org.example.mypage.dto.request.ProfilePatchRequest;
import org.example.mypage.dto.request.ProfileUpsertRequest;
import org.example.mypage.exception.OnboardingIncompleteException;
import org.example.mypage.repository.MyPageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OnboardingServiceImpl implements OnboardingService{
    private final MyPageRepository myPageRepository;


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


    /**
     * 마이페이지 프로필을 부분 수정(PATCH)합니다.
     *
     * <p><b>동작</b></p>
     * <ol>
     *   <li>{@code userId}로 삭제되지 않은 프로필({@code deletedAt IS NULL})을 조회합니다.</li>
     *   <li>프로필이 없으면 온보딩이 완료되지 않은 것으로 간주하고 {@link OnboardingIncompleteException}을 발생시킵니다.</li>
     *   <li>프로필이 존재하면 {@link Profile#patch(ProfilePatchRequest)}를 통해 요청 값만 반영합니다.</li>
     *   <li>변경 내용을 저장합니다.</li>
     * </ol>
     *
     * <p><b>예외</b></p>
     * <ul>
     *   <li>{@link OnboardingIncompleteException}: 프로필이 존재하지 않아 온보딩이 완료되지 않은 상태인 경우</li>
     * </ul>
     *
     * @param userId 사용자 식별자(ULID 문자열, JWT 쿠키 인증 기준)
     * @param req    프로필 부분 수정 요청 DTO
     * @throws OnboardingIncompleteException 온보딩(프로필 생성)이 완료되지 않은 경우
     */
    @Override
    @Transactional
    public void updateProfile(String userId, ProfilePatchRequest req) {
        Profile p = myPageRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(OnboardingIncompleteException::new);
        p.patch(req);

        myPageRepository.save(p);
    }

    @Override
    public void addOnboarding(String userId, AdditionalOnboardingAnswerRequest request) {


    }
}
