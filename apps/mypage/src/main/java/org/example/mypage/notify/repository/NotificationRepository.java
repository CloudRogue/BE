package org.example.mypage.notify.repository;

import org.example.mypage.notify.domain.NotificationSetting;
import org.example.mypage.notify.dto.EmailEnabledView;
import org.example.mypage.notify.dto.KakaoEnabledView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


/**
 * {@link NotificationSetting} 엔티티에 대한 조회/저장 기능을 제공하는 JPA Repository 입니다.
 *
 * <p>주요 목적은 사용자별 알림 채널 설정(이메일/카카오 활성화 여부 등)을 관리하는 것입니다.</p>
 *
 * <h3>식별자</h3>
 * <ul>
 *   <li>엔티티 PK: {@code Long}</li>
 *   <li>도메인 식별자: {@code userId} (ULID 문자열)</li>
 * </ul>
 *
 * <h3>조회 메서드</h3>
 * <ul>
 *   <li>{@link #findByUserId(String)}:
 *       사용자 알림 설정 엔티티 전체를 조회합니다. (없으면 {@link Optional#empty()})</li>
 *
 *   <li>{@link #findKakaoEnabledByUserId(String)}:
 *       카카오 채널 활성화 여부만 조회하는 프로젝션(뷰) 조회입니다.
 *       엔티티 전체 로딩이 불필요한 GET API 응답용으로 사용합니다.</li>
 *
 *   <li>{@link #findEmailEnabledByUserId(String)}:
 *       이메일 채널 활성화 여부만 조회하는 프로젝션(뷰) 조회입니다.
 *       엔티티 전체 로딩이 불필요한 GET API 응답용으로 사용합니다.</li>
 * </ul>
 *
 * <h3>프로젝션 타입</h3>
 * <ul>
 *   <li>{@code KakaoEnabledView}: {@code getKakaoEnabled()} 또는 유사 접근자를 노출하는 인터페이스 프로젝션</li>
 *   <li>{@code EmailEnabledView}: {@code getEmailEnabled()} 또는 유사 접근자를 노출하는 인터페이스 프로젝션</li>
 * </ul>
 *
 */
public interface NotificationRepository extends JpaRepository<NotificationSetting, Long> {
    Optional<NotificationSetting> findByUserId(String userId);

    /**
     * 사용자 ID(ULID)로 카카오 알림 채널 활성화 여부만 조회합니다(프로젝션).
     *
     * @param userId 사용자 식별자(ULID 문자열)
     * @return 카카오 활성화 여부 뷰(없으면 {@link Optional#empty()})
     */
    Optional<KakaoEnabledView> findKakaoEnabledByUserId(String userId);

    /**
     * 사용자 ID(ULID)로 이메일 알림 채널 활성화 여부만 조회합니다(프로젝션).
     *
     * @param userId 사용자 식별자(ULID 문자열)
     * @return 이메일 활성화 여부 뷰(없으면 {@link Optional#empty()})
     */
    Optional<EmailEnabledView> findEmailEnabledByUserId(String userId);

    //카카오 동의한 유저만 userId반환
    @Query("""
        select s.userId
        from NotificationSetting s
        where s.kakaoEnabled = true
    """)
    List<String> findAllUserIdsByKakaoEnabledTrue();
}
