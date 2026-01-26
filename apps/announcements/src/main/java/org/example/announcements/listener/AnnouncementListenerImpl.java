package org.example.announcements.listener;

import lombok.RequiredArgsConstructor;
import org.example.announcements.domain.Announcement;
import org.example.announcements.exception.BusinessException;
import org.example.announcements.exception.ErrorCode;
import org.example.announcements.repository.AnnouncementRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AnnouncementListenerImpl implements AnnouncementListener{

    private final AnnouncementRepository announcementRepository;

    @Override
    public List<Announcement> getNewAnnouncement() {
        //어드민 검수 안된 공고만 조회
        return announcementRepository.findByAdminCheckedFalse();
    }



    // 아이디랑 검수트루인 공고만 단건조회
    public Announcement getAnnouncement(Long announcementId){
        return announcementRepository.findByIdAndAdminCheckedFalse(announcementId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCode.ANNOUNCEMENT_NOT_FOUND,
                        "검수된 공고를 찾을 수 없습니다. announcementId=" + announcementId
                ));
    }
}
