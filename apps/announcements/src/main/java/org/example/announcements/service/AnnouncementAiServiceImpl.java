package org.example.announcements.service;

import lombok.RequiredArgsConstructor;
import org.example.announcements.dto.EligibilityDiagnoseResponse;
import org.example.announcements.dto.internal.mypage.EligibilityDiagnoseRequest;


import org.example.announcements.service.internal.ai.EligibilityAiClient;
import org.example.announcements.service.internal.mypage.MypageClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AnnouncementAiServiceImpl implements AnnouncementAiService{
    private final MypageClient mypageClient;
    private final EligibilityAiClient eligibilityAiClient;


    @Override
    public EligibilityDiagnoseResponse diagnose(long announcementId, String userId) {
        EligibilityDiagnoseRequest req = mypageClient.getDiagnose(announcementId, userId);
        return eligibilityAiClient.diagnose(req);
    }
}
