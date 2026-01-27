package org.example.mypage.profile.dto;

public interface OnboardingAnswerRow {
    Long getProfileId();
    String getTitle();
    String getType();
    String[] getOptions();
    String getValue();
    Boolean getRequiredOnboarding();
}
