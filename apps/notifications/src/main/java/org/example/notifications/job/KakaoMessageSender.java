package org.example.notifications.job;

import lombok.extern.slf4j.Slf4j;
import org.example.notifications.dto.NotificationTarget;
import org.example.notifications.dto.NotificationTargetButton;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.ObjectMapper;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class KakaoMessageSender {

    private final RestClient kakaoRestClient;

    //카카오톡 api 경로
    private final String sendToMePath;

    //내 웹 url
    private final String appBaseUrl;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public KakaoMessageSender(
            RestClient kakaoRestClient,
            @Value("${external.kakao.send-to-me-path}") String sendToMePath,
            @Value("${app.base-url}") String appBaseUrl
    ) {
        this.kakaoRestClient = kakaoRestClient;
        this.sendToMePath = sendToMePath;
        this.appBaseUrl = appBaseUrl;
    }

    //카카오서버에 나에게 보내기 요청을 날리는 메서드
    public void sendToMe(NotificationTarget target, String accessToken) {

        // 카카오가 요구하는 template_object 만들기
        String templateObjectJson = buildTemplateObjectJson(target);

        // 카카오가 원하는 형태로 변경
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("template_object", templateObjectJson);

        try {
            // Http 요청하기
            kakaoRestClient.post()
                    .uri(sendToMePath)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .header("Authorization", "Bearer " + accessToken)
                    .body(form)
                    .retrieve()
                    .toBodilessEntity();


            log.info("[kakao] sent: userId={}, template={}, announcementId={}",
                    target.getUserId(), target.getTemplateCode(), target.getAnnouncementId());

        } catch (Exception e) {
            log.error("[kakao] send failed: userId={}, template={}, announcementId={}",
                    target.getUserId(), target.getTemplateCode(), target.getAnnouncementId(), e);
        }
    }


    // NotificationTarget을 카카오가 원하는 template_object 구조로 바꾸기
    private String buildTemplateObjectJson(NotificationTarget target) {

        // 메시지 본문
        String text = target.getTitle() + "\n\n" + target.getBody();

        // 카카오 버튼구조로 변환
        List<Map<String, Object>> buttons = target.getButtons().stream()
                .map(this::toKakaoButton)
                .toList();

        // 대표링크 결정
        String fallbackUrl = buttons.isEmpty()
                ? appBaseUrl
                : (String) ((Map<?, ?>) buttons.get(0).get("link")).get("web_url");

        // template_object 전체 구조 구성
        Map<String, Object> template = new LinkedHashMap<>();
        template.put("object_type", "text");
        template.put("text", text);
        template.put("link", Map.of("web_url", fallbackUrl));

        // 버튼이 있을 때만 buttons 추가
        if (!buttons.isEmpty()) {
            template.put("buttons", buttons);
        }

        // Map을 JSON 문자열로 직렬화
        try {
            return objectMapper.writeValueAsString(template);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to build kakao template_object json", e);
        }
    }


    // 우리 버튼 DTO를 카카오 버튼 스펙으로 변환
    private Map<String, Object> toKakaoButton(NotificationTargetButton b) {
        return Map.of(
                "title", b.getName(),
                "link", Map.of(
                        "web_url", b.getUrl()
                )
        );
    }
}
