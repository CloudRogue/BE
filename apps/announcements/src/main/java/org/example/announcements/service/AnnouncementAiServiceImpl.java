package org.example.announcements.service;

import lombok.RequiredArgsConstructor;
import org.example.announcements.dto.EligibilityDiagnoseResponse;
import org.example.announcements.dto.internal.mypage.EligibilityDiagnoseRequest;


import org.example.announcements.service.internal.ai.EligibilityAiClient;
import org.example.announcements.service.internal.mypage.MypageClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnouncementAiServiceImpl implements AnnouncementAiService{
    private final MypageClient mypageClient;
    private final EligibilityAiClient eligibilityAiClient;


    @Override
    @Transactional(readOnly = true)
    public EligibilityDiagnoseResponse diagnose(long announcementId, String userId) {

        return new EligibilityDiagnoseResponse(
                EligibilityDiagnoseResponse.SupportStatus.ELIGIBLE,
                OffsetDateTime.now(),
                1,
                0,
                false,
                null,
                List.of(
                        new EligibilityDiagnoseResponse.TraceItem(
                                "mock",
                                true,
                                "AI 비활성화: ELIGIBLE 목 응답"
                        )
                )
        );

//        EligibilityDiagnoseRequest req = mypageClient.getDiagnose(announcementId, userId);
//        EligibilityDiagnoseResponse ai = eligibilityAiClient.diagnose(req);
//
//        return switch (ai.supportStatus()) {
//
//            case ELIGIBLE -> new EligibilityDiagnoseResponse(
//                    EligibilityDiagnoseResponse.SupportStatus.ELIGIBLE,
//                    ai.diagnosedAt(),
//                    ai.predictedRank(),
//                    ai.predictedBonusPoints(),
//                    false,
//                    null,
//                    ai.trace()
//            );
//
//            case INELIGIBLE -> new EligibilityDiagnoseResponse(
//                    EligibilityDiagnoseResponse.SupportStatus.INELIGIBLE,
//                    ai.diagnosedAt(),
//                    null,
//                    null,
//                    false,
//                    null,
//                    ai.trace()
//            );
//
//            case PENDING -> {
//                List<Long> need = mypageClient.getNeedOnboarding(announcementId, userId);
//
//                yield new EligibilityDiagnoseResponse(
//                        EligibilityDiagnoseResponse.SupportStatus.PENDING,
//                        ai.diagnosedAt(),
//                        ai.predictedRank(),
//                        ai.predictedBonusPoints(),
//                        true,
//                        (need == null ? List.of() : need),
//                        ai.trace()
//                );
//            }
//        };
    }




}
