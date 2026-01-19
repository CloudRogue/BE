package org.example.announcements.util;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

// 공고 상태 및 day 계산 유틸
public final class AnnouncementStatusUtil {

    private AnnouncementStatusUtil() {}

    // 공고 상태 게산
    public static String calcStatus(
            LocalDate startDate, // 모집 시작일
            LocalDate endDate,   // 모집 마감일
            LocalDate today      // 기준일(보통 LocalDate.now())
    ) {
        // 날짜 정보가 없으면 CLOSED로 처리
        if (startDate == null || endDate == null) return "CLOSED";

        // 오늘이 시작일보다 이전이면 접수 전
        if (today.isBefore(startDate)) return "UPCOMING";

        // 오늘이 마감일보다 이후면 마감
        if (today.isAfter(endDate)) return "CLOSED";

        // 오늘부터  마감일까지 남은 일수 계산
        long d = ChronoUnit.DAYS.between(today, endDate);

        // 만약 마감 3일 이내다?? 그럼  마감임박으로 설정
        if (d <= 3) return "DUE_SOON";

        // 그 외는 접수중
        return "OPEN";
    }

    // 디데이 계산 인데 마감까지 남은 일수 반환(OPEN,DUE_SOON전용)
    public static Integer calcDDay(
            String status,
            LocalDate endDate,
            LocalDate today
    ) {
        // endDate가 없으면 계산 불가
        if (endDate == null) return null;

        // 접수중/마감임박이 아니면 dDay는 내려주지 않도록 설정
        if (!"OPEN".equals(status) && !"DUE_SOON".equals(status)) return null;

        // 오늘부터 마감일까지 남은 일수 반환
        return (int) ChronoUnit.DAYS.between(today, endDate);
    }

    // 디데이 계산인데 접수 시작까지 남은 일수 반환(UPCOMING전용)
    public static Integer calcStartDDay(
            LocalDate startDate,
            LocalDate today
    ) {
        // startDate 없으면 계산 불가
        if (startDate == null) return null;

        if (!today.isBefore(startDate)) return null;

        // 오늘부터 시작일까지 남은 일수 반환
        return (int) ChronoUnit.DAYS.between(today, startDate);
    }
}
