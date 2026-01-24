package org.example.notifications.dto.api;

import java.util.List;

//지원자 유저아이디 목록
public record AppliedUserIds(
        List<String> userIds
) {}