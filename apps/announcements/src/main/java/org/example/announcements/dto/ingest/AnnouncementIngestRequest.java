package org.example.announcements.dto.ingest;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;


public record AnnouncementIngestRequest(

        //어떤 종류 인지에 대한 구분
        @NotBlank(message = "category는 필수입니다.")
        String category,

        //신규가 있어야 호출되게 되어있는데 신규가 있는데 아이템들이 비어있을수는 없음
        @NotEmpty(message = "items는 비어있을 수 없습니다.")
        @Valid
        List<AnnouncementIngestItem> items
) {
}
