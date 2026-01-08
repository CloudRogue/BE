package org.example.mypage.service;


import org.example.mypage.dto.request.ReminderSettingUpsertRequest;
import org.example.mypage.dto.response.NotificationSettingResponse;
import org.example.mypage.dto.response.ReminderSettingResponse;

/**
 * 사용자 알림 설정(채널 활성화/리마인더)을 조회·수정하는 서비스 인터페이스입니다.
 *
 * <p><b>범위</b></p>
 * <ul>
 *   <li>알림 채널 설정: 카카오, 이메일 활성화 여부</li>
 *   <li>리마인더 설정: 발송 시각/며칠 전 발송 등 사용자별 리마인더 옵션</li>
 * </ul>
 *
 * <p><b>식별자</b></p>
 * <ul>
 *   <li>{@code userId}: 사용자 식별자(ULID 문자열). 인증은 HttpOnly 쿠키(JWT) 기반을 전제로 합니다.</li>
 * </ul>
 *
 * <p><b>기본값 정책</b></p>
 * <ul>
 *   <li>설정 레코드가 아직 없는 경우(최초 진입 등) 구현체는 기본값을 생성/반환하거나,
 *       조회 응답에서 기본값을 내려주는 정책을 가질 수 있습니다.</li>
 * </ul>
 *
 */
public interface NotificationSettingService {

    /**
     * 카카오 알림 채널 활성화 여부를 조회합니다.
     *
     * @param userId 사용자 식별자(ULID 문자열)
     * @return 카카오 알림 채널 설정 응답
     */
    NotificationSettingResponse getKakaoEnabled(String userId);

    /**
     * 카카오 알림 채널 활성화 여부를 수정합니다.
     *
     * @param userId  사용자 식별자(ULID 문자열)
     * @param enabled 활성화 여부
     */
    void updateKakaoEnabled(String userId, boolean enabled);

    /**
     * 이메일 알림 채널 활성화 여부를 조회합니다.
     *
     * @param userId 사용자 식별자(ULID 문자열)
     * @return 이메일 알림 채널 설정 응답
     */
    NotificationSettingResponse getEmailEnabled(String userId);

    /**
     * 이메일 알림 채널 활성화 여부를 수정합니다.
     *
     * @param userId  사용자 식별자(ULID 문자열)
     * @param enabled 활성화 여부
     */
    void updateEmailEnabled(String userId, boolean enabled);

    /**
     * 리마인더 설정을 조회합니다.
     *
     * @param userId 사용자 식별자(ULID 문자열)
     * @return 리마인더 설정 응답
     */
    ReminderSettingResponse getReminderSetting(String userId);

    /**
     * 리마인더 설정을 생성 또는 수정(Upsert)합니다.
     *
     * <p>구현체는 다음과 같은 정책을 가질 수 있습니다.</p>
     * <ul>
     *   <li>기존 설정이 있으면 업데이트</li>
     *   <li>없으면 기본 설정을 생성한 뒤 요청 값으로 반영</li>
     *   <li>알림 채널이 모두 비활성화된 경우 설정 불가(정책 예외)</li>
     * </ul>
     *
     * @param userId 사용자 식별자(ULID 문자열)
     * @param req    리마인더 설정 upsert 요청 DTO
     */
    void upsertReminderSetting(String userId, ReminderSettingUpsertRequest req);
}
