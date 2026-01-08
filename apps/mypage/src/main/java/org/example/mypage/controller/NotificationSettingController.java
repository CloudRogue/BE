package org.example.mypage.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.mypage.dto.request.KakaoNotificationSettingPatchRequest;
import org.example.mypage.dto.response.KakaoNotificationSettingResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.attribute.UserPrincipal;


@RestController
@RequestMapping("/api/mypage/notification-settings")
@RequiredArgsConstructor
public class NotificationSettingController {

    @GetMapping("/mypage/notification-settings/kakao")
    public ResponseEntity<KakaoNotificationSettingResponse> getKakaoSetting(@AuthenticationPrincipal UserPrincipal principal) {

        return new KakaoNotificationSettingResponse(true);
    }

    @PatchMapping("/mypage/notification-settings/kakao")
    public ResponseEntity<Void> patchKakaoSetting_preferredPath(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody KakaoNotificationSettingPatchRequest req) {


        return ResponseEntity.noContent().build();
    }


}
