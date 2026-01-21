package org.example.admin.controller;

import lombok.RequiredArgsConstructor;
import org.example.admin.dto.response.AnnouncementAdminResponse;
import org.example.admin.dto.response.AnnouncementInboxResponse;
import org.example.admin.service.adminService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminController {
    private final adminService adminService;

    @GetMapping("/api/admin/inbox")
    public ResponseEntity<AnnouncementInboxResponse> getNewAnnouncement() {
        return ResponseEntity.ok(adminService.getNewAnnouncement());
    }

    @GetMapping("/api/admin/announcement/{announcementId}")
    public ResponseEntity<AnnouncementAdminResponse> getAdminAnnouncement(@PathVariable long announcementId){
        return ResponseEntity.ok(adminService.getAdminAnnouncement(announcementId));
    }
}
