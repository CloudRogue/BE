package org.example.mypage.profile.controller;


import lombok.RequiredArgsConstructor;
import org.example.mypage.profile.dto.request.ProfileCreateRequest;
import org.example.mypage.profile.dto.response.ProfileResponse;
import org.example.mypage.profile.service.MyPageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService myPageService;

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile(@AuthenticationPrincipal UserDetails principal){
        return ResponseEntity.ok(myPageService.getProfile(principal.getUsername()));
    }

    @PostMapping("/internal/profile")
    public ResponseEntity<Void> postProfile(@RequestParam("userId") String userId, @RequestBody ProfileCreateRequest request){
        myPageService.createProfile(userId, request.email(), request.nickname());

        return ResponseEntity.noContent().build();
    }
}
