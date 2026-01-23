package org.example.announcements.service.internal.ai;

import org.example.announcements.dto.EligibilityDiagnoseResponse;
import org.example.announcements.dto.internal.mypage.EligibilityDiagnoseRequest;

public interface EligibilityAiClient {
    EligibilityDiagnoseResponse diagnose(EligibilityDiagnoseRequest request);
}
