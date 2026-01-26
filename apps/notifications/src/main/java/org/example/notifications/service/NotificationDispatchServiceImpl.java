package org.example.notifications.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notifications.api.AnnouncementApi;
import org.example.notifications.api.AnnouncementApplicationApi;
import org.example.notifications.api.MypageNotificationConsentApi;
import org.example.notifications.domain.Notification;
import org.example.notifications.domain.NotificationButton;
import org.example.notifications.domain.NotificationTemplateCode;
import org.example.notifications.dto.NotificationTarget;
import org.example.notifications.dto.NotificationTargetButton;
import org.example.notifications.dto.api.AnnouncementIds;
import org.example.notifications.dto.api.AnnouncementSnapshot;
import org.example.notifications.dto.api.AppliedUserIds;
import org.example.notifications.repository.NotificationButtonRepository;
import org.example.notifications.repository.NotificationRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationDispatchServiceImpl implements NotificationDispatchService {
    //정렬첫번째 숫자 상수로 고정
    private static final int FIRST_SORT_ORDER = 1;

    private static final int DAYS_BEFORE_D7 = 7; // D-7 알림 기준

    //외부 조회 포트
    private final AnnouncementApi announcementApi;
    private final AnnouncementApplicationApi announcementApplicationApi;

    //마이페이지 조회 api
    private final MypageNotificationConsentApi mypageNotificationConsentApi;


    //알림 서버 내부 접근
    private final NotificationRepository notificationRepository;
    private final NotificationButtonRepository buttonRepository;

    //템플릿 설정 및 렌더
    private final NotificationTargetFactory targetFactory;



    @Override
    @Transactional
    public void runMorningBatch(LocalDate today) {

        // 카카오 동의 유저목록 딱 1회만 호출
        Set<String> allowedUsers = fetchKakaoAllowedUsers();

        // 동의 유저가 없으면 끝
        if (allowedUsers.isEmpty()) {
            log.info("[notif] morning-batch skipped: no kakao allowed users");
            return;
        }

        //  템플릿별로 필요한 공고 id 목록을 만들어서 dispatch 실행
        dispatch(
                NotificationTemplateCode.APPLY_D7,
                announcementApi.findAnnouncementIdsByEndDate(today.plusDays(DAYS_BEFORE_D7)),
                allowedUsers
        );

        dispatch(
                NotificationTemplateCode.APPLY_DDAY,
                announcementApi.findAnnouncementIdsByEndDate(today),
                allowedUsers
        );

        dispatch(
                NotificationTemplateCode.DOC_D7,
                announcementApi.findAnnouncementIdsByDocumentPublishedAt(today.plusDays(DAYS_BEFORE_D7)),
                allowedUsers
        );

        dispatch(
                NotificationTemplateCode.DOC_DDAY,
                announcementApi.findAnnouncementIdsByDocumentPublishedAt(today),
                allowedUsers
        );
    }

    // 카카오 동의 유저 목록 가져오기
    private Set<String> fetchKakaoAllowedUsers() {
        List<String> list = mypageNotificationConsentApi.findKakaoAllowedUserIds();
        if (list == null || list.isEmpty()) return Collections.emptySet();

        // 중복 제거를 위해
        return list.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());
    }


    // 공고 꺼내고 알림허용한 유저들 찾고 공고별로 처리하는 공통 파이프라인 메서드
    private void dispatch(
            NotificationTemplateCode templateCode,
            AnnouncementIds announcementIds,
            Set<String> allowedUsers
    ) {

        // 공고 리스트로 변환
        List<Long> ids = (announcementIds == null) ? List.of() : announcementIds.announcementIds();

        if (ids.isEmpty()) {
            log.info("[notif] {} no announcements", templateCode);
            return;
        }

        int createdCount = 0;   // 실제 저장 성공한 알림 수
        int skippedDup = 0;     // 유니크 충돌로 인한 스킵 수

        // 공고별 처리
        for (Long announcementId : ids) {

            // 문구/버튼 치환용 스냅샷
            AnnouncementSnapshot snap = announcementApi.getSnapshot(announcementId);

            if (snap == null) {
                log.warn("[notif] {} snapshot null, skip announcementId={}", templateCode, announcementId);
                continue;
            }

            // 해당 공고에 지원한유저들의  목록 (지원관리 기준 대상)
            AppliedUserIds applied = announcementApplicationApi.findApplicationUserIds(announcementId);
            List<String> users = (applied == null || applied.userIds() == null) ? List.of() : applied.userIds();

            if (users.isEmpty()) continue;

            // 유저별 처리
            for (String userId : users) {

                // 카카오 동의 유저만 통과
                if (!allowedUsers.contains(userId)) continue;

                // 버튼구성
                NotificationTarget target = targetFactory.create(
                        userId, announcementId, templateCode, snap
                );


                boolean created = saveNotificationWithButtons(target);

                if (created) createdCount++;
                else skippedDup++;
            }
        }

        log.info("[notif] {} created={}, skippedDup={}", templateCode, createdCount, skippedDup);
    }

    //저장 로직(유니크키가 중복처리)
    private boolean saveNotificationWithButtons(NotificationTarget target) {
        try {
            // 알림저장
            Notification saved = notificationRepository.save(Notification.create(
                    target.getUserId(),
                    target.getTemplateCode(),
                    target.getAnnouncementId(),
                    target.getTitle(),
                    target.getBody()
            ));

            // 버튼 저장
            int order = FIRST_SORT_ORDER;
            for (NotificationTargetButton b : target.getButtons()) {
                buttonRepository.save(NotificationButton.create(
                        saved, b.getName(), b.getUrl(), order++
                ));
            }

            return true;

        } catch (DataIntegrityViolationException e) {
            // 중복이면 스킵
            log.debug("[notif] duplicate skipped userId={}, template={}, announcementId={}",
                    target.getUserId(), target.getTemplateCode(), target.getAnnouncementId());
            return false;
        }
    }


}
