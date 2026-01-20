package org.example.mypage.profile.service;


import org.example.mypage.profile.dto.request.OnboardingRequest;
import org.example.mypage.profile.dto.response.OnboardingResponse;


public interface OnboardingService {

    OnboardingResponse getDetailProfile(String userId);

    void upsertOnboarding(String userId, OnboardingRequest request);
}
