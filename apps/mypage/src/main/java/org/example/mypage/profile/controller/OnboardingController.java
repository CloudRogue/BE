package org.example.mypage.profile.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.example.mypage.profile.dto.request.EligibilityAnswersRequest;
import org.example.mypage.profile.dto.request.EligibilityBatchCreateRequest;
import org.example.mypage.profile.dto.request.EligibilityDiagnoseRequest;
import org.example.mypage.profile.dto.request.OnboardingRequest;
import org.example.mypage.profile.dto.response.*;
import org.example.mypage.profile.service.OnboardingService;
import org.example.mypage.profile.service.PersonalizedAnnouncementService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
    public ResponseEntity<Void> putProfile(@AuthenticationPrincipal Jwt jwt,
                                           @Valid @RequestBody OnboardingRequest onboardingRequest) {

        onboardingService.upsertOnboarding(jwt.getSubject(), onboardingRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mypage/profile/detail")
    public ResponseEntity<OnboardingProfileResponse> getProfile(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(onboardingService.getDetailProfile(jwt.getSubject()));
    }

    @GetMapping("/required-onboardings")
    public ResponseEntity<OnboardingQuestionResponse> getRequiredOnboarding() {
        return ResponseEntity.ok(onboardingService.getRequiredQuestions());
    }

    @PostMapping("/required-onboardings")
    public ResponseEntity<OnboardingQuestionResponse> postRequiredOnboarding() {
        return ResponseEntity.ok(onboardingService.getRequiredQuestions());
    }


    @PostMapping("/onboardings")
    public ResponseEntity<Void> putOnboarding(@AuthenticationPrincipal Jwt jwt,
                                              @Valid @RequestBody OnboardingRequest onboardingRequest) {

        onboardingService.upsertOnboarding(jwt.getSubject(), onboardingRequest);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/internal/additional-onboardings")
    public ResponseEntity<AdditionalOnboardingBatchCreateResponse> createAdditionalOnboardings(
            @Valid @RequestBody EligibilityBatchCreateRequest request
    ) {
        AdditionalOnboardingBatchCreateResponse response =
                onboardingService.createBatch(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    // 내부 호출
    @GetMapping("/internal/onboardings")
    public ResponseEntity<EligibilityCatalogResponse> getOnboardingAdmin() {
        return ResponseEntity.ok(onboardingService.getEligibilityCatalog());
    }

    @PostMapping("/internal/onboardings/{announcementId}")
    public ResponseEntity<Void> postOnboarding(@PathVariable long announcementId,
                                               @RequestBody @Valid EligibilityAnswersRequest request) {
        onboardingService.saveAnnouncementOnboarding(announcementId, request);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/internal/ai-questions/{announcementId}")
    public ResponseEntity<AiQuestionsResponse> getAiQuestions(@PathVariable long announcementId) {
        return ResponseEntity.ok(onboardingService.getAiQuestions(announcementId));
    }

    @GetMapping("/internal/personalized")
    public ResponseEntity<List<Long>> getPersonalizedAnnouncement(@RequestParam @NotBlank String userId) {
        return ResponseEntity.ok(personalizedAnnouncementService.getPersonalizedAnnouncementIds(userId));
    }

    @GetMapping("/internal/diagnose/{announcementId}")
    public ResponseEntity<EligibilityDiagnoseRequest> getDiagnose(@PathVariable long announcementId,
                                                                  @RequestParam @NotBlank String userId) {
        return ResponseEntity.ok(onboardingService.getDiagnose(announcementId, userId));
    }

    @GetMapping("/internal/need-onboarding/{announcementId}")
    public ResponseEntity<List<Long>> getNeedOnboarding(
            @PathVariable long announcementId,
            @RequestParam @NotBlank String userId
    ) {
        return ResponseEntity.ok(onboardingService.findMissingEligibilityIds(userId, announcementId));
    }


}
