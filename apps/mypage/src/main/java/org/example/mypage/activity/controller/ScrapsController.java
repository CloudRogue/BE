package org.example.mypage.activity.controller;

import lombok.RequiredArgsConstructor;
import org.example.mypage.activity.dto.response.ScrapResponse;
import org.example.mypage.activity.service.ScrapService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ScrapsController {
    private final ScrapService scrapService;

    @GetMapping("/api/mypage/scraps")
    public ResponseEntity<ScrapResponse> getScraps(@AuthenticationPrincipal UserDetails principal, @RequestParam(required = false) Long cursor, @RequestParam(defaultValue = "20") int limit){
        return ResponseEntity.ok(scrapService.getScraps(principal.getUsername(), cursor, limit));
    }

    @PostMapping("/internal/mypage/scraps")
    public ResponseEntity<Void> postScraps(@RequestParam String userId, @RequestParam Long announcementId){
        scrapService.addScrap(userId, announcementId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/internal/mypage/scraps")
    public ResponseEntity<Void> deleteScraps(@RequestParam String userId, @RequestParam Long announcementId){
        scrapService.deleteScraps(userId, announcementId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/internal/mypage/outbound")
    public ResponseEntity<Void> postOutbound(@RequestParam String userId, @RequestParam Long announcementId){
        return ResponseEntity.ok().build();
    }

}
