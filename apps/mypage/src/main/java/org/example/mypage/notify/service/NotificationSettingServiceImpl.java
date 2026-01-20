package org.example.mypage.notify.service;


import lombok.RequiredArgsConstructor;
import org.example.mypage.notify.domain.NotificationSetting;
import org.example.mypage.notify.dto.EmailEnabledView;
import org.example.mypage.notify.dto.KakaoEnabledView;
import org.example.mypage.notify.dto.ReminderSettingUpsertRequest;
import org.example.mypage.notify.dto.NotificationSettingResponse;
import org.example.mypage.notify.dto.ReminderSettingResponse;
import org.example.mypage.exception.NotificationChannelDisabledException;
import org.example.mypage.notify.repository.NotificationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


/**
 * {@link NotificationSettingService} 구현체.
 *
 * <p><b>역할</b></p>
 * <ul>
 *   <li>사용자별 알림 채널 설정(카카오/이메일 활성화) 조회 및 변경</li>
 *   <li>사용자별 리마인더 설정(발송 시각/며칠 전) 조회 및 Upsert</li>
 * </ul>
 *
 * <p><b>예외 정책</b></p>
 * <ul>
 *   <li>리마인더 Upsert 시 이메일/카카오 채널이 모두 비활성화면
 *       {@link NotificationChannelDisabledException}을 발생시킵니다(비즈니스 거절).</li>
 * </ul>
 *
 */
@Service
@RequiredArgsConstructor
public class NotificationSettingServiceImpl implements NotificationSettingService{
    private final NotificationRepository notificationRepository;

    /**
     * 카카오 알림 채널 활성화 여부를 조회합니다.
     *
     * <p><b>조회 전략</b></p>
     * <ul>
     *   <li>엔티티 전체 로딩 대신, 카카오 활성화 여부만 담은 프로젝션({@code KakaoEnabledView})으로 조회합니다.</li>
     *   <li>설정 레코드가 없으면 기본값 {@code false}를 반환합니다.</li>
     * </ul>
     *
     * @param userId 사용자 식별자(ULID 문자열)
     * @return 카카오 알림 채널 설정 응답({@code enabled})
     */
    @Transactional(readOnly = true)
    public NotificationSettingResponse getKakaoEnabled(String userId) {
        return new NotificationSettingResponse(
                notificationRepository.findKakaoEnabledByUserId(userId)
                        .map(KakaoEnabledView::isKakaoEnabled)
                        .orElse(false)
        );
    }

    /**
     * 카카오 알림 채널 활성화 여부를 변경합니다.
     *
     * <p><b>동작</b></p>
     * <ul>
     *   <li>사용자 설정 레코드가 없으면 기본 레코드를 생성합니다.</li>
     *   <li>카카오 활성화 값을 반영한 뒤 저장합니다.</li>
     * </ul>
     *
     * @param userId  사용자 식별자(ULID 문자열)
     * @param enabled 활성화 여부
     */
    @Override
    public void updateKakaoEnabled(String userId, boolean enabled) {
        NotificationSetting setting = notificationRepository.findByUserId(userId)
                .orElseGet(() -> NotificationSetting.createDefault(userId));

        setting.setKakaoEnabled(enabled);
        notificationRepository.save(setting);
    }

    /**
     * 이메일 알림 채널 활성화 여부를 조회합니다.
     *
     * <p><b>조회 전략</b></p>
     * <ul>
     *   <li>엔티티 전체 로딩 대신, 이메일 활성화 여부만 담은 프로젝션({@code EmailEnabledView})으로 조회합니다.</li>
     *   <li>설정 레코드가 없으면 기본값 {@code false}를 반환합니다.</li>
     * </ul>
     *
     * @param userId 사용자 식별자(ULID 문자열)
     * @return 이메일 알림 채널 설정 응답({@code enabled})
     */
    @Override
    @Transactional(readOnly = true)
    public NotificationSettingResponse getEmailEnabled(String userId) {
        return new NotificationSettingResponse(
                notificationRepository.findEmailEnabledByUserId(userId)
                        .map(EmailEnabledView::isEmailEnabled)
                        .orElse(false)
        );
    }

    /**
     * 이메일 알림 채널 활성화 여부를 변경합니다.
     *
     * <p><b>동작</b></p>
     * <ul>
     *   <li>사용자 설정 레코드가 없으면 기본 레코드를 생성합니다.</li>
     *   <li>이메일 활성화 값을 반영한 뒤 저장합니다.</li>
     * </ul>
     *
     * @param userId  사용자 식별자(ULID 문자열)
     * @param enabled 활성화 여부
     */
    @Override
    public void updateEmailEnabled(String userId, boolean enabled) {
        NotificationSetting setting = notificationRepository.findByUserId(userId)
                .orElseGet(() -> NotificationSetting.createDefault(userId));

        setting.setEmailEnabled(enabled);
        notificationRepository.save(setting);
    }

    /**
     * 리마인더 설정을 조회합니다.
     *
     * <p><b>응답 규칙</b></p>
     * <ul>
     *   <li>설정 레코드가 존재하면 {@code sendAtHour}, {@code reminderDaysBefore} 값을 반환합니다.</li>
     *   <li>설정 레코드가 없으면 {@code (null, null)}을 반환합니다(미설정 상태).</li>
     * </ul>
     *
     * @param userId 사용자 식별자(ULID 문자열)
     * @return 리마인더 설정 응답
     */
    @Override
    @Transactional(readOnly = true)
    public ReminderSettingResponse getReminderSetting(String userId) {
        return notificationRepository.findByUserId(userId)
                .map(s -> ReminderSettingResponse.from(
                        s.getSendAtHour(),
                        s.getReminderDaysBefore()
                ))

                .orElseGet(() -> ReminderSettingResponse.from(null, null));
    }


    /**
     * 리마인더 설정을 생성 또는 수정(Upsert)합니다.
     *
     * <p><b>선행 조건</b></p>
     * <ul>
     *   <li>이메일/카카오 알림 채널이 모두 비활성화된 경우, 리마인더 설정은 허용되지 않습니다.</li>
     * </ul>
     *
     * <p><b>동작</b></p>
     * <ul>
     *   <li>사용자 설정 레코드가 없으면 기본 레코드를 생성합니다.</li>
     *   <li>둘 다 값이 존재하면 해당 값으로 리마인더를 업데이트합니다.</li>
     * </ul>
     *
     * @param userId 사용자 식별자(ULID 문자열)
     * @param req    리마인더 설정 upsert 요청 DTO
     * @throws NotificationChannelDisabledException 이메일/카카오 채널이 모두 비활성화된 경우
     */
    @Override
    @Transactional
    public void upsertReminderSetting(String userId, ReminderSettingUpsertRequest req) {
        NotificationSetting s = notificationRepository.findByUserId(userId)
                .orElseGet(() -> NotificationSetting.createDefault(userId));

        if (!s.isEmailEnabled() && !s.isKakaoEnabled()) {
            throw new NotificationChannelDisabledException();
        }

        s.updateReminder(req.sendAtHour(), req.daysBefore());
        notificationRepository.save(s);

    }
}

