package org.example.announcements.util;

import org.example.announcements.exception.BusinessException;
import org.example.announcements.exception.ErrorCode;
import org.springframework.data.domain.KeysetScrollPosition;
import org.springframework.data.domain.ScrollPosition;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Base64;
import java.util.LinkedHashMap;


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
            // base64를 디코딩을 함
            byte[] decoded = Base64.getUrlDecoder().decode(cursor);

            // VO로 바로 자동매핑하기
            CursorPayload payload = OM.readValue(decoded, CursorPayload.class);

            // 스크롤포지션 생성
            return ScrollPosition.of(payload.keys(), payload.direction());


        }catch (Exception e){
            throw new BusinessException(ErrorCode.INVALID_CURSOR, "cursor 값이 올바르지 않습니다.");
        }
    }

    //키셋스크롤 포지션을 커서로 변경
    public static String encode(KeysetScrollPosition position) {
        if (position == null || position.isInitial()) return null;

        // VO로구성
        CursorPayload payload = new CursorPayload(
                position.getDirection(),
                new LinkedHashMap<>(position.getKeys())
        );

        try {
            // VO를 바이트화하기
            byte[] jsonBytes = OM.writeValueAsBytes(payload);

            // 바이트를 base64로 변경
            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(jsonBytes);
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
