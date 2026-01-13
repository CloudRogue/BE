package org.example.announcements.util;

import org.example.announcements.exception.BusinessException;
import org.example.announcements.exception.ErrorCode;
import org.springframework.data.domain.KeysetScrollPosition;
import org.springframework.data.domain.ScrollPosition;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;

//스크롤 포지션 <-> 커서 변환 유틸
public final class ScrollCursorCodec {

    //JSON 직렬화/역지렬화 하기위한 도구
    private static final ObjectMapper OM = new ObjectMapper();

    private ScrollCursorCodec() {}

    //초기위치 반환
    public static KeysetScrollPosition initial(){
        return ScrollPosition.keyset();
    }

    //커서를 키셋스크롤포지션으로 복원
    public static KeysetScrollPosition decodeOrThrow(String cursor){
        if (cursor == null || cursor.isBlank()) return initial();

        try{
            //base64 -> json
            String json = new String(Base64.getUrlDecoder().decode(cursor), StandardCharsets.UTF_8);
            Map<String, Object> payload = OM.readValue(json, new TypeReference<>() {});

            //페이로드에서 key와 direction추출
            Map<String, Object> keys = (Map<String, Object>) payload.get("keys");

            String dir = (String) payload.get("direction");
            ScrollPosition.Direction direction = ScrollPosition.Direction.valueOf(dir);

            return ScrollPosition.of(keys, direction);

        }catch (Exception e){
            throw new BusinessException(ErrorCode.INVALID_REQUEST, "cursor 값이 올바르지 않습니다.");
        }
    }

    //키셋스크롤 포지션을 커서로 변경
    public static String encode(KeysetScrollPosition position) {
        if (position == null || position.isInitial()) return null;

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("direction", position.getDirection().name());
        payload.put("keys", position.getKeys());

        try {
            String json = OM.writeValueAsString(payload);
            return Base64.getUrlEncoder().withoutPadding()
                    .encodeToString(json.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "커서 생성 중 서버 오류가 발생했습니다.");
        }
    }

    //윈도우에서 얻은 다음 포지션이 Keyset인지 검증하고 반환한다
    public static KeysetScrollPosition requireKeyset(ScrollPosition position) {
        if (position instanceof KeysetScrollPosition ksp) return ksp;
        throw new BusinessException(ErrorCode.INTERNAL_ERROR, "KeysetScrollPosition이 필요합니다.");
    }
}
