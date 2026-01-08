package org.example.mypage.dto.response;

public record ReminderSettingResponse(
        boolean enabled,
        Integer sendAtHour,
        Integer daysBefore
) {
    public static ReminderSettingResponse from(Integer sendAtHour, Integer daysBefore) {
        return new ReminderSettingResponse(sendAtHour != null, sendAtHour, daysBefore);
    }
}
