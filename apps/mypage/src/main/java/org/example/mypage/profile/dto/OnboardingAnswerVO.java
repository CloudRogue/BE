package org.example.mypage.profile.dto;

import jakarta.validation.constraints.NotNull;
import org.example.mypage.profile.domain.enums.UiBlockType;


import java.util.List;

public record OnboardingAnswerVO(

        @NotNull
        Long id,

        @NotNull
        String title,

        @NotNull
        UiBlockType type,

        List<String> options,
        Object value
) { }