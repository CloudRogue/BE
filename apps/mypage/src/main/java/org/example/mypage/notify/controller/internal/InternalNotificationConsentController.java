package org.example.mypage.notify.controller.internal;

import lombok.RequiredArgsConstructor;
import org.example.mypage.notify.service.internal.InternalNotificationConsentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/mypage")
public class InternalNotificationConsentController {

    private final InternalNotificationConsentService consentService;

    @GetMapping("/notification-consents/kakao/allowed-users")
    public ResponseEntity<List<String>> getKakaoAllowedUsers() {
        return ResponseEntity.ok(consentService.getKakaoAllowedUserIds());
    }
}
