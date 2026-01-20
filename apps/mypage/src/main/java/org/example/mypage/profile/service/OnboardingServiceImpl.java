package org.example.mypage.profile.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mypage.profile.domain.Eligibility;
import org.example.mypage.profile.domain.EligibilityAnswer;
import org.example.mypage.profile.domain.EligibilityOption;
import org.example.mypage.profile.dto.OnboardingAnswer;
import org.example.mypage.profile.dto.OnboardingAnswerVO;
import org.example.mypage.profile.dto.request.OnboardingRequest;
import org.example.mypage.exception.AddOnboardingException;
import org.example.mypage.exception.OnboardingIncompleteException;
import org.example.mypage.exception.enums.ErrorCode;
import org.example.mypage.profile.dto.response.OnboardingProfileResponse;
import org.example.mypage.profile.dto.response.OnboardingQuestionResponse;
import org.example.mypage.profile.repository.EligibilityAnswerRepository;
import org.example.mypage.profile.repository.EligibilityOptionRepository;
import org.example.mypage.profile.repository.EligibilityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * 온보딩(프로필/추가 온보딩 답변) 관련 비즈니스 로직을 담당하는 서비스 구현체입니다.
 *
 *
 * <p><b>역할</b></p>
 * <ul>
 *   <li>마이페이지 프로필 생성/갱신(제출) 및 부분 수정</li>
 *   <li>추가 온보딩(자격/지원조건) 질문에 대한 사용자 답변 저장</li>
 * </ul>
 *
 * 온보딩 예상 갯수는 15개 수준이지만, 운영 중 정책/질문이 늘어날 수 있다고 판단.
 * 내부 합의에 따라 별도 분리 로직은 없지만 하드 리밋을 걸어 둠
 *
 * <p><b>예외</b></p>
 * <ul>
 *   <li>{@link OnboardingIncompleteException}: 프로필이 존재하지 않아 온보딩이 완료되지 않은 경우</li>
 *   <li>{@link AddOnboardingException}: 추가 온보딩 답변 저장 과정에서 중복/미존재/타입 불일치 등 검증 실패 시</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OnboardingServiceImpl implements OnboardingService{
    private final static int ONBOARDING_HARD_LIMIT = 30;

    private final EligibilityAnswerRepository answerRepository;
    private final EligibilityRepository eligibilityRepository;
    private final EligibilityOptionRepository eligibilityOptionRepository;

    @Override
    public OnboardingProfileResponse getDetailProfile(String userId) {
        List<OnboardingAnswer> profileAnswers = answerRepository.findAllByUserId(userId);
        List<OnboardingAnswerVO> answerList = new ArrayList<>();
        List<OnboardingAnswerVO> addAnswerList = new ArrayList<>();

        if(profileAnswers.isEmpty()){
            throw new OnboardingIncompleteException();

        } else if (profileAnswers.size() > ONBOARDING_HARD_LIMIT) {

            log.warn("Onboarding answers exceeded hard limit. userId={}, size={}, hardLimit={}",
                    userId, profileAnswers.size(), ONBOARDING_HARD_LIMIT);
        }

        for(OnboardingAnswer answer : profileAnswers){
            OnboardingAnswerVO vo = new OnboardingAnswerVO(answer.profileId(), answer.title(), answer.type(), answer.options(), answer.value());

            if(answer.requiredOnboarding()){
                answerList.add(vo);
            }
            else addAnswerList.add(vo);
        }

        return new OnboardingProfileResponse(answerList, addAnswerList);
    }


    @Transactional(readOnly = true)
    public OnboardingQuestionResponse getRequiredQuestions() {
        List<Eligibility> eligibilitieList =
                eligibilityRepository.findAllByRequiredOnboardingTrueOrderByIdAsc();
        return buildResponse(eligibilitieList);
    }

    @Transactional(readOnly = true)
    public OnboardingQuestionResponse getAddQuestions() {
        List<Eligibility> eligibilitieList =
                eligibilityRepository.findAllByRequiredOnboardingFalseOrderByIdAsc();
        return buildResponse(eligibilitieList);
    }

    private OnboardingQuestionResponse buildResponse(List<Eligibility> eligibilitieList) {
        if (eligibilitieList.isEmpty()) {
            return new OnboardingQuestionResponse(List.of());
        }

        List<Long> ids = eligibilitieList.stream()
                .map(Eligibility::getId)
                .toList();

        Map<Long, List<String>> optionLabelsByEligibilityId = fetchOptionLabelsGrouped(ids);

        List<OnboardingQuestionResponse.Item> items = eligibilitieList.stream()
                .map(e -> {
                    List<String> labels = optionLabelsByEligibilityId.get(e.getId());
                    List<String> optionsOrNull = (labels == null || labels.isEmpty()) ? null : labels;

                    return new OnboardingQuestionResponse.Item(
                            e.getId(),
                            e.getTitle(),
                            e.getOnboardingDescription(),
                            e.getQuestion(),
                            e.getType().name().toLowerCase(),
                            optionsOrNull
                    );
                })
                .toList();

        return new OnboardingQuestionResponse(items);
    }

    private Map<Long, List<String>> fetchOptionLabelsGrouped(List<Long> eligibilityIds) {
        List<EligibilityOption> options =
                eligibilityOptionRepository.findAllByEligibilityIdsOrderByEligibilityIdAndDisplayOrder(eligibilityIds);

        Map<Long, List<String>> map = new HashMap<>();
        for (EligibilityOption o : options) {
            Long eligibilityId = o.getEligibility().getId();
            map.computeIfAbsent(eligibilityId, k -> new ArrayList<>())
                    .add(o.getLabel());
        }
        return map;
    }


    /**
     * 추가 온보딩 답변을 업서트(Upsert)합니다.
     *
     * <p>요청으로 들어온 (additionalOnboardingId -> Answer) 목록을 기준으로,
     * 해당 질문(Eligibility)에 대한 사용자의 답변(EligibilityAnswer)을 다음 규칙으로 저장합니다.</p>
     *
     * 해당 코드는 추가 온보딩에 대한 검증을 수행하지 않음 -> MVP 이후 추가
     *
     * <ul>
     *   <li>기존 답변이 존재하면: 값을 갱신(UPDATE)합니다.</li>
     *   <li>기존 답변이 없으면: 새 답변을 생성(INSERT)합니다.</li>
     * </ul>
     *
     * <p><b>저장 방식</b></p>
     * <ul>
     *   <li>갱신 대상/신규 생성 대상을 하나의 리스트로 합쳐 {@code saveAll()} 한 번으로 저장합니다.</li>
     *   <li>이 메서드는 트랜잭션 내에서 수행되며, 기존 엔티티는 변경 감지(dirty checking)로 UPDATE될 수 있습니다.</li>
     * </ul>
     *
     * @param userId  답변을 저장할 사용자 식별자
     * @param request 업서트할 추가 온보딩 답변 목록
     *
     * @throws AddOnboardingException
     *         <ul>
     *           <li>{@code ADD_ONBOARDING_DUPLICATE_KEY}: 요청 answers 내 additionalOnboardingId 중복</li>
     *           <li>{@code ADD_ONBOARDING_NOT_FOUND}: 존재하지 않는 질문 ID 포함</li>
     *           <li>{@code ADD_ONBOARDING_TYPE_MISMATCH}: 질문 타입과 요청 타입 불일치</li>
     *         </ul>
     */
    @Transactional
    public void upsertOnboarding(String userId, OnboardingRequest request) {

        Map<Long, OnboardingRequest.Answer> reqMap = toReqMap(request);
        Set<Long> idSet = reqMap.keySet();

        List<Eligibility> eligibilitieList = eligibilityRepository.findAllById(idSet);
        if (eligibilitieList.size() != idSet.size()) {
            throw new AddOnboardingException(ErrorCode.ADD_ONBOARDING_NOT_FOUND);
        }

        Map<Long, EligibilityAnswer> existingMap =
                loadExistingAnswerMap(userId, idSet);

        List<EligibilityAnswer> toSave = buildUpsertEntities(userId, eligibilitieList, reqMap, existingMap);

        answerRepository.saveAll(toSave);
    }

    private Map<Long, OnboardingRequest.Answer> toReqMap(OnboardingRequest request) {
        try {
            return request.answers().stream()
                    .collect(Collectors.toMap(
                            OnboardingRequest.Answer::additionalOnboardingId,
                            Function.identity()
                    ));
        } catch (IllegalStateException e) {
            throw new AddOnboardingException(ErrorCode.ADD_ONBOARDING_DUPLICATE_KEY);
        }
    }


    private Map<Long, EligibilityAnswer> loadExistingAnswerMap(String userId, Set<Long> idSet) {
        List<EligibilityAnswer> existing =
                answerRepository.findAllByUserIdAndEligibilityIdIn(userId, idSet);

        return existing.stream().collect(Collectors.toMap(
                ea -> ea.getEligibility().getId(),
                Function.identity()
        ));
    }

    private List<EligibilityAnswer> buildUpsertEntities( String userId, List<Eligibility> eligibilitieList, Map<Long, OnboardingRequest.Answer> reqMap, Map<Long, EligibilityAnswer> existingMap) {

        List<EligibilityAnswer> toSave = new ArrayList<>(eligibilitieList.size());

        for (Eligibility e : eligibilitieList) {
            OnboardingRequest.Answer a = reqMap.get(e.getId());
            if (a == null) {
                throw new AddOnboardingException(ErrorCode.ADD_ONBOARDING_NOT_FOUND);
            }

            if (e.getType() != a.type()) {
                throw new AddOnboardingException(ErrorCode.ADD_ONBOARDING_TYPE_MISMATCH);
            }

            JsonNode newValue = a.value();

            EligibilityAnswer old = existingMap.get(e.getId());
            if (old != null) {
                old.changeValue(newValue);
                toSave.add(old);
            } else {
                toSave.add(EligibilityAnswer.create(userId, e, newValue));
            }
        }

        return toSave;
    }

}
