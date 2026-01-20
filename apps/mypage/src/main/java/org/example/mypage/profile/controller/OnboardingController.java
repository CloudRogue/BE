package org.example.mypage.profile.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.mypage.profile.dto.request.OnboardingRequest;
import org.example.mypage.profile.dto.response.OnboardingResponse;
import org.example.mypage.profile.service.OnboardingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.nio.file.attribute.UserPrincipal;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class OnboardingController {
    private final OnboardingService onboardingService;

    @PutMapping("/mypage/profile/detail")
    public ResponseEntity<Void> putProfile(@AuthenticationPrincipal UserPrincipal userPrincipal,
                                           @Valid @RequestBody OnboardingRequest onboardingRequest){

        onboardingService.upsertOnboarding(userPrincipal.getName(), onboardingRequest);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mypage/profile/detail")
    public ResponseEntity<OnboardingResponse> getProfile(@AuthenticationPrincipal UserPrincipal userPrincipal){
        return ResponseEntity.ok(onboardingService.getDetailProfile(userPrincipal.getName()));
    }
}
