package org.example.mypage.notify.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.mypage.notify.dto.EmailNotificationSettingPatchRequest;
import org.example.mypage.notify.dto.KakaoNotificationSettingPatchRequest;
import org.example.mypage.notify.dto.ReminderSettingUpsertRequest;
import org.example.mypage.notify.dto.NotificationSettingResponse;
import org.example.mypage.notify.dto.ReminderSettingResponse;
import org.example.mypage.notify.service.NotificationSettingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipal;


@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class NotificationSettingController {
    private final NotificationSettingService notificationSettingService;


    @GetMapping("/notification-settings/kakao")
    public ResponseEntity<NotificationSettingResponse> getKakaoSetting(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(notificationSettingService.getKakaoEnabled(jwt.getSubject()));
    }

    @PatchMapping("/notification-settings/kakao")
    public ResponseEntity<Void> patchKakaoSetting(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody KakaoNotificationSettingPatchRequest req
    ) {
        notificationSettingService.updateKakaoEnabled(jwt.getSubject(), req.enabled());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/notification-settings/email")
    public ResponseEntity<NotificationSettingResponse> getEmailSetting(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(notificationSettingService.getEmailEnabled(jwt.getSubject()));
    }

    @PatchMapping("/notification-settings/email")
    public ResponseEntity<Void> patchEmailSetting(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody EmailNotificationSettingPatchRequest req
    ) {
        notificationSettingService.updateEmailEnabled(jwt.getSubject(), req.enabled());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/notification-settings/reminder-settings")
    public ResponseEntity<ReminderSettingResponse> getReminder(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(notificationSettingService.getReminderSetting(jwt.getSubject()));
    }

    @PutMapping("/notification-settings/reminder-settings")
    public ResponseEntity<Void> putReminder(
            @AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ReminderSettingUpsertRequest req
    ) {
        notificationSettingService.upsertReminderSetting(jwt.getSubject(), req);
        return ResponseEntity.noContent().build();
    }

}
