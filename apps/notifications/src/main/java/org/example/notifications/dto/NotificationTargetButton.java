package org.example.notifications.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class NotificationTargetButton {

    private final String name;
    private final String url;
}
