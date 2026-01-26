package org.example.mypage.profile.service;


import lombok.RequiredArgsConstructor;
import org.example.mypage.exception.OnboardingIncompleteException;
import org.example.mypage.profile.domain.AnnouncementEligibility;
import org.example.mypage.profile.domain.Eligibility;
import org.example.mypage.profile.domain.EligibilityAnswer;
import org.example.mypage.profile.domain.enums.UiBlockType;
import org.example.mypage.profile.repository.AnnouncementEligibilityRepository;
import org.example.mypage.profile.repository.EligibilityAnswerRepository;
import org.example.mypage.profile.repository.EligibilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;


import java.util.*;

@Service
@RequiredArgsConstructor
public class PersonalizedAnnouncementServiceImpl implements PersonalizedAnnouncementService {

    private final EligibilityRepository eligibilityRepository;
    private final EligibilityAnswerRepository eligibilityAnswerRepository;
    private final AnnouncementEligibilityRepository announcementEligibilityRepository;

@Transactional(readOnly = true)
public List<Long> getPersonalizedAnnouncementIds(String userId) {

    long requiredCount = eligibilityRepository.countByRequiredOnboardingTrue();
    if (requiredCount == 0) {
        return List.of();
    }

    List<Long> requiredIds = eligibilityRepository.findRequiredOnboardingIds();

    long answeredCount = eligibilityAnswerRepository.countByUserIdAndEligibilityIds(userId, requiredIds);
    if (answeredCount != requiredCount) {
        throw new OnboardingIncompleteException();
    }

    List<Long> eligibleRequiredIds = eligibilityRepository.findRequiredOnboardingBooleanOrSingleIds();
    if (eligibleRequiredIds.isEmpty()) {
        return List.of();
    }


    List<EligibilityAnswer> answers =
            eligibilityAnswerRepository.findAllByUserIdWithEligibility(userId, eligibleRequiredIds);

    Map<Long, JsonNode> userAnswerMap = new HashMap<>();
    for (EligibilityAnswer a : answers) {
        Long eid = a.getEligibility().getId();

        userAnswerMap.putIfAbsent(eid, a.getValue());
    }

    List<AnnouncementEligibility> allConditions =
            announcementEligibilityRepository.findAllRequiredBoolOrSingleConditionsFetch();

    Map<Long, Integer> satisfiedCountByAnnouncement = new HashMap<>();
    Map<Long, Integer> requiredCountByAnnouncement = new HashMap<>();

    for (AnnouncementEligibility ae : allConditions) {
        Eligibility e = ae.getEligibility();
        UiBlockType type = e.getType();

        long announcementId = ae.getAnnouncementId();
        requiredCountByAnnouncement.merge(announcementId, 1, Integer::sum);

        JsonNode expected = ae.getExpectedValue();
        JsonNode actual = userAnswerMap.get(e.getId());

        if (matches(type, expected, actual)) {
            satisfiedCountByAnnouncement.merge(announcementId, 1, Integer::sum);
        }
    }

    List<Long> result = new ArrayList<>();
    for (Map.Entry<Long, Integer> entry : requiredCountByAnnouncement.entrySet()) {
        long announcementId = entry.getKey();
        int requiredForAnnouncement = entry.getValue();
        int satisfied = satisfiedCountByAnnouncement.getOrDefault(announcementId, 0);

        if (satisfied == requiredForAnnouncement) {
            result.add(announcementId);
        }
    }

    result.sort(Long::compareTo);
    return result;
}

private boolean matches(UiBlockType type, JsonNode expected, JsonNode actual) {

    if (type == UiBlockType.BOOLEAN) {
        return expected != null && expected.isBoolean()
                && actual != null && actual.isBoolean()
                && expected.booleanValue() == actual.booleanValue();
    }

    if (type == UiBlockType.SELECT_SINGLE) {
        return expected != null && expected.isTextual()
                && actual != null && actual.isTextual()
                && expected.textValue().equals(actual.textValue());
    }

    return false;
}
}
