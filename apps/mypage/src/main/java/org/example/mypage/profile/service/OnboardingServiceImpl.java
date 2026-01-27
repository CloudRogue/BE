package org.example.mypage.profile.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.mypage.exception.AdminOnboardingException;
import org.example.mypage.profile.domain.AnnouncementEligibility;
import org.example.mypage.profile.domain.Eligibility;
import org.example.mypage.profile.domain.EligibilityAnswer;
import org.example.mypage.profile.domain.EligibilityOption;
import org.example.mypage.profile.domain.enums.UiBlockType;
import org.example.mypage.profile.dto.OnboardingAnswerRow;
import org.example.mypage.profile.dto.OnboardingAnswerVO;
import org.example.mypage.profile.dto.request.*;
import org.example.mypage.exception.AddOnboardingException;
import org.example.mypage.exception.OnboardingIncompleteException;
import org.example.mypage.exception.enums.ErrorCode;
import org.example.mypage.profile.dto.response.*;
import org.example.mypage.profile.repository.AnnouncementEligibilityRepository;
import org.example.mypage.profile.repository.EligibilityAnswerRepository;
import org.example.mypage.profile.repository.EligibilityOptionRepository;
import org.example.mypage.profile.repository.EligibilityRepository;
import org.example.mypage.util.JsonBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

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

    private final AnnouncementEligibilityRepository announcementEligibilityRepository;
    private final EligibilityAnswerRepository answerRepository;
    private final EligibilityRepository eligibilityRepository;
    private final EligibilityOptionRepository eligibilityOptionRepository;
    private final ObjectMapper objectMapper;

    @Override
    public OnboardingProfileResponse getDetailProfile(String userId) {
        List<OnboardingAnswerRow> profileAnswers = answerRepository.findAllByUserId(userId);

        List<OnboardingAnswerVO> answerList = new ArrayList<>();
        List<OnboardingAnswerVO> addAnswerList = new ArrayList<>();

        if (profileAnswers.isEmpty()) throw new OnboardingIncompleteException();
        if (profileAnswers.size() > ONBOARDING_HARD_LIMIT) {
            log.warn("Onboarding answers exceeded hard limit. userId={}, size={}, hardLimit={}",
                    userId, profileAnswers.size(), ONBOARDING_HARD_LIMIT);
        }

        for (OnboardingAnswerRow answer : profileAnswers) {
            com.fasterxml.jackson.databind.JsonNode valueNode = JsonBridge.fromText(answer.getValue());
            Object value = objectMapper.convertValue(valueNode, Object.class);

            List<String> options = (answer.getOptions() == null) ? null : Arrays.asList(answer.getOptions());

            OnboardingAnswerVO vo = new OnboardingAnswerVO(
                    answer.getProfileId(),
                    answer.getTitle(),
                    UiBlockType.valueOf(answer.getType()),
                    options,
                    value
            );

            if (Boolean.TRUE.equals(answer.getRequiredOnboarding())) answerList.add(vo);
            else addAnswerList.add(vo);
        }

        return new OnboardingProfileResponse(answerList, addAnswerList);
    }


    @Transactional
    public AdditionalOnboardingBatchCreateResponse createBatch(EligibilityBatchCreateRequest req) {

        List<Eligibility> entities = req.items().stream()
                .map(i -> {
                    Eligibility e = Eligibility.of(
                            i.title(),
                            i.description(),
                            i.question(),
                            "qwer",
                            i.type()
                    );
                    if (i.options() != null) {
                        tools.jackson.databind.JsonNode toolsTree =
                                objectMapper.valueToTree(i.options());

                        com.fasterxml.jackson.databind.JsonNode jacksonTree =
                                JsonBridge.toFasterxml(toolsTree);

                        e.setValue(jacksonTree);
                    } else {
                        e.setValue(null);
                    }

                    return e;
                })
                .toList();

        List<Eligibility> saved = eligibilityRepository.saveAll(entities);

        List<AdditionalOnboardingBatchCreateResponse.Item> data = saved.stream()
                .map(e -> new AdditionalOnboardingBatchCreateResponse.Item(
                        e.getId(),
                        e.getTitle(),
                        e.getOnboardingDescription(),
                        e.getQuestion(),
                        e.getType().name(),
                        false
                ))
                .toList();

        return new AdditionalOnboardingBatchCreateResponse(data);
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

    @Transactional
    public EligibilityCatalogResponse getEligibilityCatalog() {
        List<Eligibility> eligibilities = eligibilityRepository.findAll();

        List<EligibilityCatalogResponse.Item> items = eligibilities.stream()
                .map(e -> new EligibilityCatalogResponse.Item(
                        e.getId(),
                        e.getTitle(),
                        e.getOnboardingDescription(),
                        e.getQuestion(),
                        e.getType(),
                        e.isRequiredOnboarding()
                ))
                .toList();

        return new EligibilityCatalogResponse(items);
    }

    @Transactional
    public void saveAnnouncementOnboarding(long announcementId, EligibilityAnswersRequest request) {

        if (announcementEligibilityRepository.existsByAnnouncementId(announcementId)) {
            throw new AdminOnboardingException(ErrorCode.ADMIN_ONBOARDING_ALREADY_CREATED);
        }

        List<OnboardingPostRequest.Answer> answers = request.eligibility().answers();
        if (answers == null || answers.isEmpty()) {
            throw new AdminOnboardingException(ErrorCode.ADMIN_ONBOARDING_EMPTY_ANSWERS);
        }

        List<Long> ids = answers.stream()
                .map(OnboardingPostRequest.Answer::additionalOnboardingId)
                .toList();

        Set<Long> idSet = new HashSet<>(ids);
        if (idSet.size() != ids.size()) {
            throw new AdminOnboardingException(ErrorCode.ADMIN_ONBOARDING_DUPLICATE_KEY);
        }

        List<Eligibility> eligibilityList = eligibilityRepository.findAllById(idSet);
        if (eligibilityList.size() != idSet.size()) {
            throw new AdminOnboardingException(ErrorCode.ADMIN_ONBOARDING_NOT_FOUND);
        }

        Map<Long, Eligibility> eligibilityMap = new HashMap<>();
        for (Eligibility e : eligibilityList) {
            eligibilityMap.put(e.getId(), e);
        }

        List<AnnouncementEligibility> toCreateMappings = new ArrayList<>(answers.size());

        for (OnboardingPostRequest.Answer a : answers) {
            Long eligibilityId = a.additionalOnboardingId();
            Eligibility eligibility = eligibilityMap.get(eligibilityId);

            com.fasterxml.jackson.databind.JsonNode value =
                    JsonBridge.toFasterxml(a.value());

            toCreateMappings.add(AnnouncementEligibility.of(announcementId, eligibility, value));
        }

        announcementEligibilityRepository.saveAll(toCreateMappings);
    }

    @Transactional(readOnly = true)
    public AiQuestionsResponse getAiQuestions(long announcementId) {

        List<AiQuestionsResponse.Question> questions = eligibilityRepository
                .findAllByOrderByIdAsc()
                .stream()
                .map(e -> new AiQuestionsResponse.Question(
                        e.getTitle(),
                        e.getOnboardingDescription(),
                        e.getQuestion()
                ))
                .toList();

        return new AiQuestionsResponse(questions);
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


    @Transactional(readOnly = true)
    public EligibilityDiagnoseRequest getDiagnose(long announcementId, String userId) {


        List<AnnouncementEligibility> conditions =
                announcementEligibilityRepository.findAllByAnnouncementIdFetch(announcementId);

        if (conditions == null || conditions.isEmpty()) {
            throw new IllegalArgumentException("no requirements for announcementId=" + announcementId);
        }

        List<Long> eligibilityIds = new ArrayList<>(conditions.size());
        List<EligibilityDiagnoseRequest.RequirementItem> requirements = new ArrayList<>(conditions.size());

        for (AnnouncementEligibility ae : conditions) {
            Eligibility e = ae.getEligibility();
            if (e == null || e.getId() == null) {
                throw new IllegalStateException("announcementEligibility.eligibility is null. announcementId=" + announcementId);
            }

            Long additionalOnboardingId = e.getId();
            String key = e.getQuestion();

            com.fasterxml.jackson.databind.JsonNode expectedNode = ae.getExpectedValue();

            if (expectedNode == null || expectedNode.isNull()) {
                throw new IllegalArgumentException("expectedValue must not be null. additionalOnboardingId=" + additionalOnboardingId);
            }

            final String expectedValueStr;
            if (expectedNode.isTextual() || expectedNode.isBoolean() || expectedNode.isNumber()) {
                expectedValueStr = expectedNode.asText();
            } else {
                try {
                    expectedValueStr = objectMapper.writeValueAsString(expectedNode);
                } catch (Exception ex) {
                    throw new IllegalArgumentException("failed to serialize expectedValue. additionalOnboardingId=" + additionalOnboardingId, ex);
                }
            }

            if (expectedValueStr == null || expectedValueStr.isBlank()) {
                throw new IllegalArgumentException("expectedValue must be non-blank. additionalOnboardingId=" + additionalOnboardingId);
            }

            requirements.add(new EligibilityDiagnoseRequest.RequirementItem(
                    additionalOnboardingId,
                    key,
                    expectedValueStr
            ));
            eligibilityIds.add(additionalOnboardingId);
        }

        // 성능 이유 있을듯 나중에 수정
        List<EligibilityAnswer> answers =
                answerRepository.findAllByUserIdWithEligibility(userId, eligibilityIds);

        Map<Long, EligibilityAnswer> answerMap = new HashMap<>();
        if (answers != null) {
            for (EligibilityAnswer a : answers) {
                if (a == null || a.getEligibility() == null || a.getEligibility().getId() == null) continue;
                answerMap.putIfAbsent(a.getEligibility().getId(), a);
            }
        }

        List<EligibilityDiagnoseRequest.AnswerItem> answerItems = new ArrayList<>(eligibilityIds.size());

        for (Long eligibilityId : eligibilityIds) {
            EligibilityAnswer a = answerMap.get(eligibilityId);

            String answerValueStr = null;
            if (a != null) {
                var v = a.getValue();
                if (v != null && !v.isNull()) {
                    if (v.isTextual() || v.isNumber() || v.isBoolean()) {

                        answerValueStr = v.asText();
                    } else {

                        try {
                            answerValueStr = objectMapper.writeValueAsString(v);
                        } catch (Exception e) {
                            throw new IllegalArgumentException("failed to serialize answer value", e);
                        }
                    }
                }
            }

            answerItems.add(new EligibilityDiagnoseRequest.AnswerItem(eligibilityId, answerValueStr));
        }

        return new EligibilityDiagnoseRequest(requirements, answerItems);
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

            com.fasterxml.jackson.databind.JsonNode newValue =
                    JsonBridge.toFasterxml(a.value());

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




}
