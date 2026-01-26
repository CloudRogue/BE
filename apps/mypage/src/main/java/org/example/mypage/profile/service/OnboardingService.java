package org.example.mypage.profile.service;


import org.example.mypage.profile.dto.request.EligibilityAnswersRequest;
import org.example.mypage.profile.dto.request.EligibilityBatchCreateRequest;
import org.example.mypage.profile.dto.request.EligibilityDiagnoseRequest;
import org.example.mypage.profile.dto.request.OnboardingRequest;
import org.example.mypage.profile.dto.response.*;


public interface OnboardingService {

    OnboardingProfileResponse getDetailProfile(String userId);
    void upsertOnboarding(String userId, OnboardingRequest request);
    OnboardingQuestionResponse getRequiredQuestions();
    OnboardingQuestionResponse getAddQuestions();
    AiQuestionsResponse getAiQuestions(long announcementId);
    void saveAnnouncementOnboarding(long announcementId, EligibilityAnswersRequest request);
    EligibilityDiagnoseRequest getDiagnose(long announcementId, String userId);
    EligibilityCatalogResponse getEligibilityCatalog();
    AdditionalOnboardingBatchCreateResponse createBatch(EligibilityBatchCreateRequest req);
}
