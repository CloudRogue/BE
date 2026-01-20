package org.example.mypage.activity.controller;

import lombok.RequiredArgsConstructor;
import org.example.mypage.activity.dto.response.ScrapResponse;
import org.example.mypage.activity.service.ScrapService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/mypage")
@RequiredArgsConstructor
public class ScrapsController {
    private final ScrapService scrapService;

    @GetMapping("/scraps")
    public ResponseEntity<ScrapResponse> getScraps(@AuthenticationPrincipal UserDetails principal, @RequestParam(required = false) Long cursor, @RequestParam(defaultValue = "20") int limit){
        return ResponseEntity.ok(scrapService.getScraps(principal.getUsername(), cursor, limit));
    }



}
