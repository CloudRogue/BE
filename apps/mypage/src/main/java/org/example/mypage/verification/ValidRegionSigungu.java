package org.example.mypage.verification;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


/**
 * 시/군/구(및 자치구) 문자열 유효성을 검증하는 커스텀 Bean Validation 제약 애노테이션입니다.
 *
 * <h2>검증 내용</h2>
 * <ul>
 *   <li>{@link RegionSigunguValidator}를 통해 입력값이 코드북(화이트리스트)에 포함되는지 검사합니다.</li>
 *   <li>null 허용 여부, 공백 처리 등 세부 규칙은 Validator 구현에 따릅니다.</li>
 * </ul>
 *
 * <h2>적용 범위</h2>
 * <ul>
 *   <li>필드: {@link ElementType#FIELD}</li>
 *   <li>메서드 파라미터: {@link ElementType#PARAMETER}</li>
 * </ul>
 *
 * <h2>메시지</h2>
 * <ul>
 *   <li>기본 메시지: "유효하지 않은 시군구 입니다."</li>
 *   <li>프로젝트 메시지 번들(i18n)을 통해 치환/현지화할 수 있습니다.</li>
 * </ul>
 */
@Documented
@Constraint(validatedBy = RegionSigunguValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRegionSigungu {
    String message() default "유효하지 않은 시군구 입니다.";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
