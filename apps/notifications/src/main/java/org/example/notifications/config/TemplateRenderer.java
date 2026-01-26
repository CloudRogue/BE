package org.example.notifications.config;

import org.example.notifications.dto.api.AnnouncementSnapshot;
import org.springframework.stereotype.Component;

@Component
public class TemplateRenderer {

    public String render(String raw, AnnouncementSnapshot snapshot) {
        if (raw == null) return null;

        return raw
                .replace("{title}", safe(snapshot.title()))
                .replace("{applyUrl}", safe(snapshot.applyUrl()));
    }


    private String safe(String v) {
        return v == null ? "" : v;
    }
}
