package org.example.mypage.profile.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.example.mypage.profile.dto.request.EligibilityAnswersRequest;
import org.example.mypage.profile.dto.request.EligibilityDiagnoseRequest;
import org.example.mypage.profile.dto.request.OnboardingRequest;
import org.example.mypage.profile.dto.response.AiQuestionsResponse;
import org.example.mypage.profile.dto.response.EligibilityCatalogResponse;
import org.example.mypage.profile.dto.response.OnboardingProfileResponse;
import org.example.mypage.profile.dto.response.OnboardingQuestionResponse;
import org.example.mypage.profile.service.OnboardingService;
import org.example.mypage.profile.service.PersonalizedAnnouncementService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OnboardingController {
    private final OnboardingService onboardingService;
    private final PersonalizedAnnouncementService personalizedAnnouncementService;

    @PutMapping("/mypage/profile/detail")
    public ResponseEntity<Void> putProfile(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                           @Valid @RequestBody OnboardingRequest onboardingRequest){

        onboardingService.upsertOnboarding(userPrincipal.getName(), onboardingRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mypage/profile/detail")
    public ResponseEntity<OnboardingProfileResponse> getProfile(@AuthenticationPrincipal UserPrincipal userPrincipal){
        return ResponseEntity.ok(onboardingService.getDetailProfile(userPrincipal.getName()));
    }

    @GetMapping("/required-onboarding")
    public ResponseEntity<OnboardingQuestionResponse> getRequiredOnboarding(){
        return ResponseEntity.ok(onboardingService.getRequiredQuestions());
    }

    @GetMapping("/onboarding")
    public ResponseEntity<OnboardingQuestionResponse> getAddOnboarding(){
        return ResponseEntity.ok(onboardingService.getAddQuestions());
    }

    //프론트엔드 요청으로 경로 분리
    @PutMapping("/onboarding")
    public ResponseEntity<Void> putOnboarding(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                           @Valid @RequestBody OnboardingRequest onboardingRequest){

        onboardingService.upsertOnboarding(userPrincipal.getName(), onboardingRequest);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/internal/onboarding")
    public ResponseEntity<EligibilityCatalogResponse> getOnboardingAdmin(){
        return ResponseEntity.ok(onboardingService.getEligibilityCatalog());
    }

    @PostMapping("/internal/onboarding/{announcementId}")
    public ResponseEntity<Void> postOnboarding(@PathVariable long announcementId, @RequestBody @Valid EligibilityAnswersRequest request){
        onboardingService.saveAnnouncementOnboarding(announcementId, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/internal/ai-questions/{announcementId}")
    public ResponseEntity<AiQuestionsResponse> getAiQuestions(@PathVariable long announcementId){
        return ResponseEntity.ok(onboardingService.getAiQuestions(announcementId));
    }

    @Validated
    @GetMapping("/internal/personalized")
    public ResponseEntity<List<Long>> getPersonalizedAnnouncement(@RequestParam @NotBlank String userId){
        return ResponseEntity.ok(personalizedAnnouncementService.getPersonalizedAnnouncementIds(userId));
    }

    @Validated
    @GetMapping("/internal/diagnose/{announcementId}")
    public ResponseEntity<EligibilityDiagnoseRequest> getDiagnose(@PathVariable long announcementId, @RequestParam @NotBlank String userId){
        return ResponseEntity.ok(onboardingService.getDiagnose(announcementId, userId));
    }



}
