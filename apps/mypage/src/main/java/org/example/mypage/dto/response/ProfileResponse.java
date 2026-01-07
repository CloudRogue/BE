package org.example.mypage.dto.response;


import lombok.Builder;
import org.example.mypage.domain.enums.Gender;
import org.example.mypage.domain.enums.HouseholdRole;

import java.time.LocalDate;

@Builder
public record ProfileResponse(
        String name,
        Gender gender,
        LocalDate birthDate,
        String regionSigungu,
        Integer householdSize,
        HouseholdRole householdRole,
        Integer incomeDecile,
        Boolean onboardingCompleted
) {}
