package org.example.mypage.profile.service;

import java.util.List;

public interface PersonalizedAnnouncementService {
    List<Long> getPersonalizedAnnouncementIds(String userId);
}
