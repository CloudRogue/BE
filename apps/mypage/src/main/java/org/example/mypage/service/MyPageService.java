package org.example.mypage.service;

import org.example.mypage.dto.response.ProfileResponse;

public interface MyPageService {

    ProfileResponse getProfile(String userId);
}
