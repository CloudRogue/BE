package org.example.mypage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.mypage.dto.request.AdditionalOnboardingAnswerRequest;
import org.example.mypage.dto.request.ProfilePatchRequest;
import org.example.mypage.dto.request.ProfileUpsertRequest;
import org.example.mypage.service.OnboardingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OnboardingController {
    private final OnboardingService onboardingService;

    @PutMapping("/mypage/profile")
    public ResponseEntity<Void> putProfile(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                           @Valid @RequestBody ProfileUpsertRequest profileUpsertRequest){

        onboardingService.submitProfile(userPrincipal.getName(), profileUpsertRequest);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/mypage/profile")
    public ResponseEntity<Void> patchProfile(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                             @Valid @RequestBody ProfilePatchRequest patchRequest){

        onboardingService.updateProfile(userPrincipal.getName(), patchRequest);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/additional-onboardings")
    public ResponseEntity<Void> addOnboarding(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                              @Valid AdditionalOnboardingAnswerRequest request){

        onboardingService.addOnboarding(userPrincipal.getName(), request);
        return ResponseEntity.noContent().build();
    }
}
