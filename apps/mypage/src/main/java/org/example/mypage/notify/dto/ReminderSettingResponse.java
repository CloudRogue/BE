package org.example.mypage.notify.dto;

public record ReminderSettingResponse(
        boolean enabled,
        Integer sendAtHour,
        Integer daysBefore
) {
    public static ReminderSettingResponse from(Integer sendAtHour, Integer daysBefore) {
        return new ReminderSettingResponse(sendAtHour != null, sendAtHour, daysBefore);
    }
}
