package org.example.notifications.service;

import lombok.RequiredArgsConstructor;
import org.example.notifications.config.NotificationTemplatesProperties;
import org.example.notifications.config.TemplateRenderer;
import org.example.notifications.domain.NotificationTemplateCode;
import org.example.notifications.dto.NotificationTarget;
import org.example.notifications.dto.NotificationTargetButton;
import org.example.notifications.dto.api.AnnouncementSnapshot;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationTargetFactory {

    private final NotificationTemplatesProperties templates;
    private final TemplateRenderer renderer;

    //상태와 공고 스냅샷으로 최종 문구/버튼 생성
    public NotificationTarget create(
            String userId,
            Long announcementId,
            NotificationTemplateCode templateCode,
            AnnouncementSnapshot snap
    ) {
        NotificationTemplatesProperties.Template t = selectTemplate(templateCode);

        String renderedTitle = renderer.render(t.title(), snap);
        String renderedBody = renderer.render(t.body(), snap);

        List<NotificationTargetButton> buttons = toButtons(t, snap);

        return new NotificationTarget(
                userId,
                announcementId,
                templateCode,
                renderer.render(t.title(), snap),
                renderer.render(t.body(), snap),
                buttons
        );
    }


    //템플릿 선택 로직
    private NotificationTemplatesProperties.Template selectTemplate(NotificationTemplateCode code) {
        return switch (code) {
            case APPLY_D7 -> templates.applyD7();
            case APPLY_DDAY -> templates.applyDday();
            case DOC_D7 -> templates.docD7();
            case DOC_DDAY -> templates.docDday();
        };
    }

    //얌파일을 버튼으로 변환
    private List<NotificationTargetButton> toButtons(
            NotificationTemplatesProperties.Template t,
            AnnouncementSnapshot snap
    ) {
        //순서유지하면서 리스트로 모아두기
        var raws = List.of(
                new RawButton(t.button1Name(), t.button1Url()),
                new RawButton(t.button2Name(), t.button2Url()),
                new RawButton(t.button3Name(), t.button3Url()),
                new RawButton(t.button4Name(), t.button4Url()),
                new RawButton(t.button5Name(), t.button5Url())
        );

        // 만약 값이 있다?? 치환하고 변환
        return raws.stream()
                .filter(b -> hasText(b.name()) && hasText(b.url()))
                // 치환 후 변환
                .map(b -> new NotificationTargetButton(
                        renderer.render(b.name(), snap),
                        renderer.render(b.url(), snap)
                ))
                .toList();
    }

    //if문에서 복잡함을 막기위해 사용
    private record RawButton(String name, String url) {}

    //검증유틸
    private boolean hasText(String s) {
        return s != null && !s.isBlank();
    }

}
