package org.example.notifications.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

//버튼을 최대 5개로 제한
@ConfigurationProperties(prefix = "notifications.templates")
public record NotificationTemplatesProperties(
        Template applyD7,
        Template applyDday,
        Template docD7,
        Template docDday
) {
    public record Template(
            String title,
            String body,

            String button1Name, String button1Url,
            String button2Name, String button2Url,
            String button3Name, String button3Url,
            String button4Name, String button4Url,
            String button5Name, String button5Url
    ) {}
}