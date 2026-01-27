package org.example.mypage.profile.dto;


import org.example.mypage.profile.domain.enums.UiBlockType;


import java.util.List;
public record OnboardingAnswer (
    Long profileId,
    String title,
    UiBlockType type,
    List<String> options,
    Object value,
    boolean requiredOnboarding

){}
