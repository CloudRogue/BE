package org.example.mypage.dto.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;




/**
 * 리마인더 설정을 생성/수정(Upsert)하기 위한 요청 DTO.
 *
 * <p><b>의미</b></p>
 * <ul>
 *   <li>{@code sendAtHour}와 {@code daysBefore}가 <b>모두 null</b>이면 리마인더를 <b>해제(미설정)</b>합니다.</li>
 *   <li>{@code sendAtHour}와 {@code daysBefore}가 <b>모두 값이 있으면</b> 해당 값으로 리마인더를 <b>설정</b>합니다.</li>
 *   <li>둘 중 하나만 null인 요청은 <b>잘못된 요청</b>으로 간주합니다.</li>
 * </ul>
 *
 * <h3>필드</h3>
 * <ul>
 *   <li>{@code sendAtHour}: 리마인더 발송 시각(시). 0~23 범위의 정수</li>
 *   <li>{@code daysBefore}: 기준일(예: 공고 마감/일정) 기준 며칠 전에 보낼지. 0~365 범위의 정수</li>
 * </ul>
 *
 * <h3>검증</h3>
 * <ul>
 *   <li>{@link Min}/{@link Max}: {@code sendAtHour}는 0~23 범위여야 합니다(값이 존재하는 경우).</li>
 *   <li>{@link Min}/{@link Max}: {@code daysBefore}는 0~365 범위여야 합니다(값이 존재하는 경우).</li>
 *   <li>{@link AssertTrue}: 두 필드는 <b>둘 다 설정되거나</b> <b>둘 다 null</b>이어야 합니다.</li>
 * </ul>
 *
 */
public record ReminderSettingUpsertRequest(

        @Min(0) @Max(23)
        Integer sendAtHour,


        @Min(0) @Max(365)
        Integer daysBefore
) {

        /**
         * {@code sendAtHour}와 {@code daysBefore}는 둘 다 설정되거나, 둘 다 null이어야 합니다.
         *
         * <p>둘 중 하나만 null이면 의미가 불명확하므로 잘못된 요청으로 간주합니다.</p>
         *
         * @return 두 필드가 모두 null이거나 모두 non-null이면 {@code true}
         */
        @AssertTrue(message = "sendAtHour와 daysBefore는 둘 다 설정하거나 둘 다 NULL이 되어야 합니다.")
        public boolean isPairValid() {
                return (sendAtHour == null && daysBefore == null)
                        || (sendAtHour != null && daysBefore != null);
        }

}
