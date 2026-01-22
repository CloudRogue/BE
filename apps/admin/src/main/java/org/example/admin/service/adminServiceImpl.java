package org.example.admin.service;

import lombok.RequiredArgsConstructor;
import org.example.admin.api.AIApi;
import org.example.admin.api.AnnouncementApi;
import org.example.admin.api.MyPageApi;
import org.example.admin.dto.request.AIApiRequest;
import org.example.admin.dto.response.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class adminServiceImpl implements adminService {
    private final AnnouncementApi announcementApi;
    private final AIApi aiApi;
    private final MyPageApi myPageApi;


    @Override
    public AnnouncementInboxResponse getNewAnnouncement() {
        return announcementApi.getNewAnnouncement();
    }

    @Override
    public AnnouncementAdminResponse getAdminAnnouncement(long announcementId) {

        AnnouncementAdminMaterialResponse m = announcementApi.getAdminMaterial(announcementId);
        AiQuestionsResponse qs = myPageApi.getAiQuestions(announcementId);

        AIApiRequest req = new AIApiRequest(
                m.url(),
                m.publisher(),
                toAIApiQuestions(qs)
        );

        List<AIApiResponse> digests = aiApi.ingest(req);

        return new AnnouncementAdminResponse(
                m.announcementId(),
                m.publisher(),
                m.title(),
                m.housingType(),
                m.supplyType(),
                m.startDate(),
                m.endDate(),
                m.documentPublishedAt(),
                m.finalPublishedAt(),
                null,          // status
                0,             // dDay
                m.rentGtn(),
                m.enty(),
                m.prtpay(),
                m.surlus(),
                m.mtRntchrg(),
                m.fullAdres(),
                m.refrnLegaldongNm(),
                m.url(),
                m.applyUrl(),
                false,         // isScrapped
                toKvDigestItems(digests)
        );
    }

    private static List<AIApiRequest.Question> toAIApiQuestions(AiQuestionsResponse qs) {
        if (qs == null || qs.questions() == null) return List.of();
        return qs.questions().stream()
                .map(q -> new AIApiRequest.Question(
                        q.title(),
                        q.description(),
                        q.question()
                ))
                .toList();
    }

    private static List<AnnouncementAdminResponse.KvDigestItem> toKvDigestItems(List<AIApiResponse> digests) {
        if (digests == null) return List.of();
        return digests.stream()
                .map(d -> new AnnouncementAdminResponse.KvDigestItem(
                        d.getTitle(),
                        d.getValue()
                ))
                .toList();
    }
}



