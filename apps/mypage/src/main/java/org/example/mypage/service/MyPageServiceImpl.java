package org.example.mypage.service;

import lombok.RequiredArgsConstructor;
import org.example.mypage.domain.Profile;
import org.example.mypage.dto.response.ProfileResponse;
import org.example.mypage.exception.OnboardingIncompleteException;
import org.example.mypage.repository.MyPageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class MyPageServiceImpl implements MyPageService{
    private final MyPageRepository myPageRepository;

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
}
