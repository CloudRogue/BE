package org.example.mypage.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.example.mypage.domain.enums.Gender;
import org.example.mypage.domain.enums.HouseholdRole;
import org.example.mypage.verification.ValidRegionSigungu;

import java.time.LocalDate;

/**
 * 프로필 부분 수정(PATCH) 요청 DTO입니다.
 *
 * <h2>특징</h2>
 * <ul>
 *   <li>(null이면 "변경하지 않음")</li>
 *   <li>요청에 포함된 값 중 null이 아닌 항목만 프로필 엔티티에 반영됩니다.</li>
 *   <li>정의되지 않은 JSON 필드는 무시합니다. ({@link JsonIgnoreProperties#ignoreUnknown()})</li>
 * </ul>
 *
 * <h2>검증 규칙</h2>
 * <ul>
 *   <li>{@code name}: 최대 10자</li>
 *   <li>{@code regionSigungu}: 최대 100자 + {@link ValidRegionSigungu} 커스텀 검증</li>
 *   <li>{@code householdSize}: 1~100</li>
 *   <li>{@code incomeDecile}: 1~10</li>
 * </ul>
 *
 * <h2>주의</h2>
 * <ul>
 *   <li>PATCH 의미상, null은 "값을 비운다"가 아니라 "수정하지 않는다"로 해석됩니다.</li>
 * </ul>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProfilePatchRequest(

        @Size(max = 10)
        String name,

        Gender gender,

        LocalDate birthDate,

        @Size(max = 100)
        @ValidRegionSigungu
        String regionSigungu,

        @Min(1)
        @Max(100)
        Integer householdSize,

        HouseholdRole householdRole,

        @Min(1)
        @Max(10)
        Integer incomeDecile
) {}