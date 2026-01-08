package org.example.mypage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.mypage.dto.request.EmailNotificationSettingPatchRequest;
import org.example.mypage.dto.request.KakaoNotificationSettingPatchRequest;
import org.example.mypage.dto.request.ReminderSettingUpsertRequest;
import org.example.mypage.dto.response.ReminderSettingResponse;
import org.example.mypage.service.NotificationSettingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipal;


@RestController
@RequestMapping("/api/mypage")
@RequiredArgsConstructor
public class NotificationSettingController {
    private final NotificationSettingService notificationSettingService;


    @GetMapping("/notification-settings/kakao")
    public ResponseEntity<Boolean> getKakaoSetting(@AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(notificationSettingService.getKakaoEnabled(principal.getName()));
    }

    @PatchMapping("/notification-settings/kakao")
    public ResponseEntity<Void> patchKakaoSetting(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody KakaoNotificationSettingPatchRequest req) {

        notificationSettingService.updateKakaoEnabled(principal.getName(), req.enabled());
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/notification-settings/email")
    public ResponseEntity<Boolean> getEmailSetting(@AuthenticationPrincipal UserPrincipal principal) {

        return ResponseEntity.ok(notificationSettingService.getEmailEnabled(principal.getName()));
    }

    @PatchMapping("/notification-settings/email")
    public ResponseEntity<Void> patchEmailSetting(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody EmailNotificationSettingPatchRequest req) {

        notificationSettingService.updateEmailEnabled(principal.getName(), req.enabled());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/reminder-settings")
    public ResponseEntity<ReminderSettingResponse> getReminder(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(notificationSettingService.getReminderSetting(principal.getName()));
    }

    @PutMapping("/reminder-settings")
    public ResponseEntity<Void> putReminder(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody ReminderSettingUpsertRequest req
    ) {
        notificationSettingService.upsertReminderSetting(principal.getName(), req);
        return ResponseEntity.noContent().build();
    }

}
