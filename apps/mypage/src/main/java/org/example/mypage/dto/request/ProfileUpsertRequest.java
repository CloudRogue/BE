package org.example.mypage.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import org.example.mypage.domain.enums.Gender;
import org.example.mypage.domain.enums.HouseholdRole;
import org.example.mypage.verification.ValidRegionSigungu;

import java.time.LocalDate;


/**
 * 프로필 생성 요청 DTO입니다.
 *
 * <h2>의도</h2>
 * <ul>
 *   <li>프로필이 없으면 생성(Create)하고, 있으면 전체 값을 갱신(Update)하는 요청에 사용합니다.</li>
 *   <li>PATCH와 달리 모든 핵심 필드가 필수이며, 누락 시 검증 오류(400)가 발생합니다.</li>
 * </ul>
 *
 * <h2>역직렬화 정책</h2>
 * <ul>
 *   <li>정의되지 않은 JSON 필드는 무시합니다. ({@link JsonIgnoreProperties#ignoreUnknown()})</li>
 * </ul>
 *
 * <h2>검증 규칙</h2>
 * <ul>
 *   <li>{@code name}: 공백 불가, 최대 10자</li>
 *   <li>{@code gender}: 필수</li>
 *   <li>{@code birthDate}: 필수</li>
 *   <li>{@code regionSigungu}: 공백 불가, 최대 100자 + {@link ValidRegionSigungu} 커스텀 검증</li>
 *   <li>{@code householdSize}: 필수, 1~100</li>
 *   <li>{@code householdRole}: 필수</li>
 *   <li>{@code incomeDecile}: 필수, 1~10</li>
 * </ul>
 *
 * <h2>주의</h2>
 * <ul>
 *   <li>UPSERT 성격상, 이 DTO는 "현재 저장된 값의 일부만 변경" 용도가 아닙니다.</li>
 *   <li>부분 수정이 필요하면 별도의 PATCH DTO(예: ProfilePatchRequest)를 사용하세요.</li>
 * </ul>
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record ProfileUpsertRequest(

        @NotBlank
        @Size(max = 10)
        String name,

        @NotNull
        Gender gender,

        @NotNull
        LocalDate birthDate,

        @NotBlank
        @Size(max = 100)
        @ValidRegionSigungu
        String regionSigungu,

        @NotNull
        @Min(1)
        @Max(100)
        Integer householdSize,

        @NotNull
        HouseholdRole householdRole,

        @NotNull
        @Min(1)
        @Max(10)
        Integer incomeDecile
) {}
