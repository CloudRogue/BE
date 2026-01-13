package org.example.announcements.dto.ingest;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.announcements.domain.AnnouncementSource;

import java.time.LocalDate;

//파싱서버가 메인서버로 보내는 공고
public record AnnouncementIngestItem(
        @NotNull(message = "source는 필수입니다.")
        AnnouncementSource source,

        // MYHOME: "pblancId:houseSn", SH_RSS: "seq"
        @NotBlank(message = "externalKey는 필수입니다.")
        String externalKey,

        String title,
        String publisher,
        String housingType,
        String supplyType,
        String regionName,

        LocalDate startDate,
        LocalDate endDate,
        LocalDate documentPublishedAt,
        LocalDate finalPublishedAt,

        String applyUrl,

        Long rentGtn,
        Long enty,
        Long prtpay,
        Long surlus,
        Long mtRntchrg,

        String fullAddress,
        String refrnLegaldongNm

) {}
