package org.example.notifications.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notifications.api.AnnouncementApi;
import org.example.notifications.api.AnnouncementApplicationApi;
import org.example.notifications.domain.Notification;
import org.example.notifications.domain.NotificationButton;
import org.example.notifications.domain.NotificationPreference;
import org.example.notifications.domain.NotificationTemplateCode;
import org.example.notifications.dto.NotificationTarget;
import org.example.notifications.dto.NotificationTargetButton;
import org.example.notifications.dto.api.AnnouncementIds;
import org.example.notifications.dto.api.AnnouncementSnapshot;
import org.example.notifications.dto.api.AppliedUserIds;
import org.example.notifications.repository.NotificationButtonRepository;
import org.example.notifications.repository.NotificationPreferenceRepository;
import org.example.notifications.repository.NotificationRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationDispatchServiceImpl implements NotificationDispatchService {
    //정렬첫번째 숫자 상수로 고정
    private static final int FIRST_SORT_ORDER = 1;

    //외부 조회 포트
    private final AnnouncementApi announcementApi;
    private final AnnouncementApplicationApi announcementApplicationApi;

    //알림 서버 내부 접근
    private final NotificationPreferenceRepository preferenceRepository;
    private final NotificationRepository notificationRepository;
    private final NotificationButtonRepository buttonRepository;

    //템플릿 설정 및 렌더
    private final NotificationTargetFactory targetFactory;

    //접수마감 D-7
    @Override
    @Transactional
    public void sendApplyD7(LocalDate today) {
        dispatch(NotificationTemplateCode.APPLY_D7,
                announcementApi.findAnnouncementIdsByEndDate(today.plusDays(7)));
    }

    //접수마감 D-Day
    @Override
    @Transactional
    public void sendApplyDDay(LocalDate today) {
        dispatch(NotificationTemplateCode.APPLY_DDAY,
                announcementApi.findAnnouncementIdsByEndDate(today));
    }

    //서류발표 D-7
    @Override
    @Transactional
    public void sendDocD7(LocalDate today) {
        dispatch(NotificationTemplateCode.DOC_D7,
                announcementApi.findAnnouncementIdsByDocumentPublishedAt(today.plusDays(7)));
    }

    //서류발표 D-Day
    @Override
    @Transactional
    public void sendDocDDay(LocalDate today) {
        dispatch(NotificationTemplateCode.DOC_DDAY,
                announcementApi.findAnnouncementIdsByDocumentPublishedAt(today));
    }

    // 공고 꺼내고 알림허용한 유저들 찾고 공고별로 처리하는 공통 파이프라인 메서드
    private void dispatch(NotificationTemplateCode templateCode, AnnouncementIds announcementIds) {

        // 공고 리스트로 변환
        List<Long> ids = announcementIds.announcementIds();

        if (ids.isEmpty()) {
            log.info("[notif] {} no announcements", templateCode);
            return;
        }

        // 허용한 유저들로 조회
        Set<String> allowedUsers = preferenceRepository.findAllByAllowedTrue().stream()
                .map(NotificationPreference::getUserId)
                .collect(Collectors.toSet());


        if (allowedUsers.isEmpty()) {
            log.info("[notif] {} no allowed users", templateCode);
            return;
        }

        int createdCount = 0;   // 실제로 저장 성공한 알림수
        int skippedDup = 0;     // 유니크 충돌로 인한 스킵수

        //  공고별 처리
        for (Long announcementId : ids) {

            // 문구/버튼 치환용 스냅샷
            AnnouncementSnapshot snap = announcementApi.getSnapshot(announcementId);

            AppliedUserIds applied = announcementApplicationApi.findApplicationUserIds(announcementId);
            List<String> users = applied.userIds();

            // 담은 유저가 없으면 다음 공고로
            if (users.isEmpty()) continue;

            // 유저별 처리
            for (String userId : users) {

                // 알림 허용 유저만
                if (!allowedUsers.contains(userId)) continue;

                // 팩토리로 구성
                NotificationTarget target = targetFactory.create(
                        userId, announcementId, templateCode, snap
                );

                boolean created = saveNotificationWithButtons(target);

                if (created) createdCount++;
                else skippedDup++;
            }
        }

        // 결과 로그
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
