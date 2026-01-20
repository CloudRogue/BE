package org.example.mypage.profile.service;


import org.example.mypage.profile.dto.request.OnboardingRequest;
import org.example.mypage.profile.dto.response.OnboardingProfileResponse;
import org.example.mypage.profile.dto.response.OnboardingQuestionResponse;


public interface OnboardingService {

    OnboardingProfileResponse getDetailProfile(String userId);
    void upsertOnboarding(String userId, OnboardingRequest request);
    OnboardingQuestionResponse getRequiredQuestions();
    OnboardingQuestionResponse getAddQuestions();

}
