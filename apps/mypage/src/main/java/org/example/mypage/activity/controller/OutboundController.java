package org.example.mypage.activity.controller;

import lombok.RequiredArgsConstructor;
import org.example.mypage.activity.dto.response.OutboundResponse;
import org.example.mypage.activity.service.OutboundScrollFacade;
import org.example.mypage.activity.service.OutboundService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class OutboundController {
    private final OutboundScrollFacade outboundScrollFacade;
    private final OutboundService outboundService;

    @GetMapping("/internal/mypage/outbound")
    public ResponseEntity<OutboundResponse> getOutbound(
            @AuthenticationPrincipal Jwt jwt,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") int limit
    ) {
        String userId = jwt.getSubject(); // sub
        return ResponseEntity.ok(outboundScrollFacade.getOutbound(userId, cursor, limit));
    }

    @PostMapping("/internal/mypage/outbound")
    public ResponseEntity<Void> postOutbound(
            @RequestParam String userId,
            @RequestParam Long announcementId
    ) {
        outboundService.recordOutbound(userId, announcementId);
        return ResponseEntity.ok().build();
    }
}
