package org.example.announcements.controller.internal;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.announcements.dto.ingest.AnnouncementIngestRequest;
import org.example.announcements.exception.ErrorResponse;
import org.example.announcements.service.ingest.AnnouncementIngestService;
import org.example.announcements.service.ingest.IngestResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 파싱서버가 신규 공고가 있을 때 HTTPS로 호출하는 내부 API임
@Tag(name = "Internal - Announcement Ingest", description = "파싱서버(배치) → 메인서버 공고 적재용 내부 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/announcements")
public class AnnouncementIngestController {

    private final AnnouncementIngestService ingestService;

    @Operation(
            summary = "공고 ingest(업서트 적재)",
            description = """
                파싱서버가 6시간 주기 배치로 공고를 파싱한 뒤,
                신규 공고가 존재하는 경우 메인서버로 HTTPS 요청을 보내 적재합니다.
                
                - 내부 서버간 통신 용도이며 외부 공개 API가 아닙니다.
                - 요청 body의 items에는 공고 목록이 포함됩니다.
                - 스코프는 메인서버에서 env값을 사용할예정입니다
                - sh는 카테고리를 고정값으로 사용합니다
            """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "적재 처리 성공",
                    content = @Content(schema = @Schema(implementation = IngestResult.class))),
            @ApiResponse(responseCode = "400", description = "요청 값 검증 실패",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    //파싱서버에서 메인서버 공고 적재
    @PostMapping("/ingest")
    public ResponseEntity<IngestResult> ingest(@RequestBody @Valid AnnouncementIngestRequest request) {
        IngestResult result = ingestService.ingest(request.category(), request.items());
        return ResponseEntity.ok(result);
    }

}
