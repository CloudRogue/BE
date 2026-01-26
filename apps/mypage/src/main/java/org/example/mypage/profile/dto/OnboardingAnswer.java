package org.example.mypage.profile.dto;


import org.example.mypage.profile.domain.enums.UiBlockType;
import tools.jackson.databind.JsonNode;

import java.util.List;
public record OnboardingAnswer (
    Long profileId,
    String title,
    UiBlockType type,
    List<String> options,
    JsonNode value,
    boolean requiredOnboarding

){}
