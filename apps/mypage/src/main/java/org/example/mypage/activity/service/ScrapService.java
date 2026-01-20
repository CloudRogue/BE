package org.example.mypage.activity.service;

import org.example.mypage.activity.dto.response.ScrapResponse;

public interface ScrapService {
    ScrapResponse getScrap(String userId);
}
