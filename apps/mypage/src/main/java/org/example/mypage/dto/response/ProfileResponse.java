package org.example.mypage.dto.response;


import lombok.Builder;
import org.example.mypage.domain.enums.Gender;
import org.example.mypage.domain.enums.HouseholdRole;

import java.time.LocalDate;


/**
 * 프로필 조회/응답 DTO입니다.
 *
 * <h2>용도</h2>
 * <ul>
 *   <li>마이페이지 프로필 조회 API의 응답으로 사용됩니다.</li>
 *   <li>클라이언트가 화면에 표시할 프로필 값들과 온보딩 완료 여부를 함께 제공합니다.</li>
 * </ul>
 *
 * <h2>필드 의미</h2>
 * <ul>
 *   <li>{@code name}: 사용자 이름(표시명)</li>
 *   <li>{@code gender}: 성별</li>
 *   <li>{@code birthDate}: 생년월일</li>
 *   <li>{@code regionSigungu}: 거주지역(시/군/구)</li>
 *   <li>{@code householdSize}: 가구원 수</li>
 *   <li>{@code householdRole}: 가구 내 역할</li>
 *   <li>{@code incomeDecile}: 소득 분위(1~10 가정)</li>
 *   <li>{@code onboardingCompleted}: 온보딩(프로필 입력) 완료 여부</li>
 * </ul>
 *
 */
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
