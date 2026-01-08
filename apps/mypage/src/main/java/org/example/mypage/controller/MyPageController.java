package org.example.mypage.controller;


import lombok.RequiredArgsConstructor;
import org.example.mypage.dto.response.ProfileResponse;
import org.example.mypage.service.MyPageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class MyPageController {
    private final MyPageService myPageService;

    @GetMapping("/profile")
    public ResponseEntity<ProfileResponse> getProfile(@AuthenticationPrincipal UserDetails principal){
        return ResponseEntity.ok(myPageService.getProfile(principal.getUsername()));
    }


}
