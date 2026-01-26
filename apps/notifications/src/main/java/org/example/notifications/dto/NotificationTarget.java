package org.example.notifications.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.notifications.domain.NotificationTemplateCode;

import java.util.List;

//발송
@Getter
@RequiredArgsConstructor
public class NotificationTarget {

    private final String userId;
    private final Long announcementId;
    private final NotificationTemplateCode templateCode; // 어떤 알림인지

    private final String title;
    private final String body;

    private final List<NotificationTargetButton> buttons;




}
