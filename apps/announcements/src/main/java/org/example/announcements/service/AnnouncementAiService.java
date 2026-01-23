package org.example.announcements.service;

import org.example.announcements.dto.EligibilityDiagnoseResponse;

public interface AnnouncementAiService {
    EligibilityDiagnoseResponse diagnose(long announcementId, String userId);
}
