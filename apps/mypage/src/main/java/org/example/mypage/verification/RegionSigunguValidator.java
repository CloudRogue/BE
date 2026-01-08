package org.example.mypage.verification;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.example.mypage.util.RegionSigunguCodebook;
import org.springframework.stereotype.Component;


/**
 * {@link ValidRegionSigungu} 커스텀 제약을 검증하는 Validator 입니다.
 *
 * <h2>검증 대상</h2>
 * <ul>
 *   <li>시/군/구(및 자치구) 문자열 필드 (예: {@code regionSigungu})</li>
 * </ul>
 *
 * <h2>검증 규칙</h2>
 * <ul>
 *   <li>null은 유효로 간주합니다. (선택 필드/PATCH 시나리오 지원)</li>
 *   <li>공백 문자열은 무효입니다.</li>
 *   <li>그 외 값은 {@link RegionSigunguCodebook}의 유효 목록에 포함되는지 검사합니다.</li>
 * </ul>
 *
 * <h2>정규화</h2>
 * <ul>
 *   <li>검증 전 {@link String#trim()}을 적용합니다.</li>
 *   <li>추가 공백 축약 등은 {@link RegionSigunguCodebook} 내부 정규화 로직을 따릅니다.</li>
 * </ul>
 *
 */
@Component
public class RegionSigunguValidator implements ConstraintValidator<ValidRegionSigungu, String> {

    /**
     * 시/군/구 문자열이 유효한지 검증합니다.
     *
     * @param value 검증할 문자열(사용자 입력)
     * @param context 제약 검증 컨텍스트
     * @return 유효하면 true, 무효하면 false
     */
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) return true;
        if (value.isBlank()) return false;

        return RegionSigunguCodebook.isValid(value.trim());
    }
}
