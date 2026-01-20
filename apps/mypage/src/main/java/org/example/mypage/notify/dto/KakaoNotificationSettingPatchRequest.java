package org.example.mypage.notify.dto;

import jakarta.validation.constraints.NotNull;


/**
 * 카카오 알림 채널 활성화 여부를 수정하기 위한 요청 DTO.
 *
 * <p>PATCH /api/mypage/notification-settings/kakao 요청 바디로 사용됩니다.</p>
 *
 * <h3>필드</h3>
 * <ul>
 *   <li>{@code enabled}: 카카오 알림 채널 활성화 여부</li>
 * </ul>
 *
 * <h3>검증</h3>
 * <ul>
 *   <li>{@link NotNull}: {@code enabled}는 반드시 포함되어야 합니다.</li>
 * </ul>
 */
public record KakaoNotificationSettingPatchRequest(
        @NotNull Boolean enabled
) {}
