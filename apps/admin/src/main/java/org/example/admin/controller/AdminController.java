package org.example.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.admin.api.EligibilityCatalogResponse;
import org.example.admin.dto.request.AnnouncementDetailRequest;
import org.example.admin.dto.request.EligibilityBatchCreateRequest;
import org.example.admin.dto.response.AdditionalOnboardingBatchCreateResponse;
import org.example.admin.dto.response.AnnouncementAdminResponse;
import org.example.admin.dto.response.AnnouncementInboxResponse;
import org.example.admin.service.adminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AdminController {
    private final adminService adminService;

    @GetMapping("/api/admin/inboxes")
    public ResponseEntity<AnnouncementInboxResponse> getNewAnnouncement() {
        return ResponseEntity.ok(adminService.getNewAnnouncement());
    }

    @GetMapping("/api/admin/announcements/{announcementId}")
    public ResponseEntity<AnnouncementAdminResponse> getAdminAnnouncement(@PathVariable long announcementId){
        return ResponseEntity.ok(adminService.getAdminAnnouncement(announcementId));
    }

    @PostMapping("/api/admin/announcements/{announcementId}")
    public ResponseEntity<Void> postAdminAnnouncement(@PathVariable long announcementId, @RequestBody AnnouncementDetailRequest request){
        adminService.postAdminAnnouncement(announcementId, request);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/api/admin/additional-onboardings")
    public ResponseEntity<EligibilityCatalogResponse> getEligibility(){
        return ResponseEntity.ok(adminService.getOnboardingAdminCatalog());
    }

    @PostMapping("/api/admin/additional-onboardings")
    public ResponseEntity<AdditionalOnboardingBatchCreateResponse> createAdditionalOnboardings(
            @Valid @RequestBody EligibilityBatchCreateRequest request
    ) {
        AdditionalOnboardingBatchCreateResponse response = adminService.getOnboarding(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
