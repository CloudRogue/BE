package org.example.announcements.redis;

import lombok.NoArgsConstructor;
import org.example.announcements.domain.AnnouncementSource;

import java.util.Locale;

@NoArgsConstructor
public final class SeenStdIdMapper {

    private static final String MYHOME_PREFIX = "myhome"; // myhome:{category}:{pblancId}:{houseSn}
    private static final String SH_RSS_PREFIX = "sh:rss"; // sh:rss:{seq}
    private static final String SEP = ":";


    public static String toStdId(AnnouncementSource source, String category, String externalKey){

        // source나 externalKey가 없으면 생성불가
        if (source == null) return null;
        if (externalKey == null || externalKey.isBlank()) return null;

        // 키정리 및 소문자화
        String ek = externalKey.trim();
        String cat = (category == null) ? null : category.trim().toLowerCase(Locale.ROOT);

        // 소스별 규칙 분기
        return switch (source) {

            case MYHOME -> {
                if (cat == null || cat.isBlank()) yield null;

                // myhome:{category}:{pblancId}:{houseSn} 형태
                yield MYHOME_PREFIX + SEP + cat + SEP + ek;
            }

            case SH_RSS -> {
                // stdId 멤버는 sh:rss:{seq}
                yield SH_RSS_PREFIX + SEP + ek;
            }
        };


    }
}
