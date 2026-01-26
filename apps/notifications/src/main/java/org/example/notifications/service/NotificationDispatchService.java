package org.example.notifications.service;

import java.time.LocalDate;

public interface NotificationDispatchService {

    //매일 아침 9시에 알림 배치 실행
    void runMorningBatch(LocalDate today);

}
