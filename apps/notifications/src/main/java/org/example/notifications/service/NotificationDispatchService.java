package org.example.notifications.service;

import java.time.LocalDate;

public interface NotificationDispatchService {

    //공고 접수 마감 D-7
    void sendApplyD7(LocalDate today);

    //공고 접수 마감 D-Day
    void sendApplyDDay(LocalDate today);

    //서류발표 D-7
    void sendDocD7(LocalDate today);

    //서류발표 D-Day
    void sendDocDDay(LocalDate today);

}
