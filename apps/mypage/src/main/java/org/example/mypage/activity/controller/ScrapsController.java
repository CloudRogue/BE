package org.example.mypage.activity.controller;

import lombok.RequiredArgsConstructor;
import org.example.mypage.activity.dto.response.ScrapResponse;
import org.example.mypage.activity.service.ScrapScrollFacade;
import org.example.mypage.activity.service.ScrapService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ScrapsController {
    private final ScrapService scrapService;
    private final ScrapScrollFacade scrapScrollFacade;

    @GetMapping("/api/mypage/scraps")
    public ResponseEntity<ScrapResponse> getScraps(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") int limit
    ) {
        return ResponseEntity.ok(scrapScrollFacade.getScraps(jwt.getSubject(), cursor, limit));
    }

    @PostMapping("/api/mypage/scraps")
    public ResponseEntity<Void> postScrapsPublic(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam Long announcementId
    ) {
        String userId = jwt.getSubject();
        scrapService.addScrap(userId, announcementId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/api/mypage/scraps")
    public ResponseEntity<Void> deleteScrapsPublic(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam Long announcementId
    ) {
        String userId = jwt.getSubject();
        scrapService.deleteScraps(userId, announcementId);
        return ResponseEntity.noContent().build();
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
}