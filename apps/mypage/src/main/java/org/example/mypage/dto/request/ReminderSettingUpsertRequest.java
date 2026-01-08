package org.example.mypage.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record ReminderSettingUpsertRequest(

        @Min(0) @Max(23)
        Integer sendAtHour,


        @Min(0) @Max(365)
        Integer daysBefore
) {
}
