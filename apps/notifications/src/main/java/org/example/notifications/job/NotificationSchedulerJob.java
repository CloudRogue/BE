package org.example.notifications.job;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.notifications.service.NotificationDispatchService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationSchedulerJob {

    private final NotificationDispatchService dispatchService;

    //매일 오전 9시에 실행
    @Scheduled(cron = "0 0 9 * * *")
    public void runDaily() {
        LocalDate today = LocalDate.now(); // 오늘 기준으로 날짜 계산
        log.info("[notif-job] start, today={}", today);

        // 접수기간 알림 2개
        dispatchService.sendApplyD7(today);
        dispatchService.sendApplyDDay(today);

        // 서류발표 알림 2개
        dispatchService.sendDocD7(today);
        dispatchService.sendDocDDay(today);

        log.info("[notif-job] end, today={}", today);
    }

}
