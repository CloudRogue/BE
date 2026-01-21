package org.example.announcements.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "announcements",
        uniqueConstraints = { // 중복 방지 유니크
                @UniqueConstraint( // MYHOME 같은 경우 pblancId+houseSn이 실질 식별자 SH는 seq가 식별자
                        name = "uq_ann_source_external_key", // 제약 이름
                        columnNames = {"source", "external_key"} // 유니크하게 설정
                )})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Announcement {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id; //pk

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnnouncementSource source; // 출처(MYHOME/SH_RSS)

    @NotBlank
    @Column(name = "external_key", nullable = false) // 유니크 키로 사용
    private String externalKey; // 외부식별자 (예: MYHOME= pblancId:houseSn, SH=seq)

    @Column
    private String title; // 공고명

    @NotBlank
    @Column(name = "publisher", nullable = false)
    private String publisher; // 발행처

    @Column(name = "housing_type")
    private String housingType; // 주택 유형

    @Column(name = "supply_type")
    private String supplyType; // 공급유형

    @Column(name = "region_code")
    private String regionCode; // 지역코드

    @Column(name = "region_name")
    private String regionName; // 시군구명

    @Column(name = "start_date")
    private LocalDate startDate; // 모집 시작일

    @Column(name = "end_date")
    private LocalDate endDate; // 모집 종료일

    @Column(name = "document_published_at")
    private LocalDate documentPublishedAt;// 서류발표날짜

    @Column(name = "final_published_at")
    private LocalDate finalPublishedAt; // 당첨자 발표날짜


    //----------URL-------------

    @Column(name = "apply_url", length = 1000)
    private String applyUrl; // 공고url

    @Column(name = "apply_entry_url", length = 1000)
    private String applyEntryUrl; // 신청하러가기 링크


    //-------- 금액 관련 정보들------------
    @Column(name = "rent_gtn")
    private Long rentGtn; // 임대보증금(최소)

    @Column(name = "enty")
    private Long enty; // 계약금(최소)

    @Column(name = "prtpay")
    private Long prtpay;// 중도금(최소)

    @Column(name = "surlus")
    private Long surlus;// 잔금(최소)

    @Column(name = "mt_rntchrg")
    private Long mtRntchrg;// 월임대료(최소)

    //-------------주소 정보--------------
    @Column(name = "full_address", length = 255)
    private String fullAddress;// 전체주소

    @Column(name = "refrn_legaldong_nm")
    private String refrnLegaldongNm; // 법정동명

    //-----------생성 수정 시간---------
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false) //  수정 불가
    private LocalDateTime createdAt; // 생성 시각

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // 수정 시각

    //어드민 체크여부 기본값이 false임
    @Column(name = "admin_checked", nullable = false)
    private boolean adminChecked;

    //정적 메서드
    public static Announcement create(
            AnnouncementSource source,
            String externalKey,
            String title,
            String publisher,
            String housingType,
            String supplyType,
            String regionCode,
            String regionName,
            LocalDate startDate,
            LocalDate endDate,
            LocalDate documentPublishedAt,
            LocalDate finalPublishedAt,
            String applyUrl,
            String applyEntryUrl,
            Long rentGtn,
            Long enty,
            Long prtpay,
            Long surlus,
            Long mtRntchrg,
            String fullAddress,
            String refrnLegaldongNm
    ) {
        Announcement a = new Announcement();
        a.source = source;
        a.externalKey = externalKey;
        a.title = title;
        a.publisher = publisher;
        a.housingType = housingType;
        a.supplyType = supplyType;
        a.regionCode = regionCode;
        a.regionName = regionName;
        a.startDate = startDate;
        a.endDate = endDate;
        a.documentPublishedAt = documentPublishedAt;
        a.finalPublishedAt = finalPublishedAt;
        a.applyUrl = applyUrl;
        a.applyEntryUrl = applyEntryUrl;
        a.rentGtn = rentGtn;
        a.enty = enty;
        a.prtpay = prtpay;
        a.surlus = surlus;
        a.mtRntchrg = mtRntchrg;
        a.fullAddress = fullAddress;
        a.refrnLegaldongNm = refrnLegaldongNm;
        a.adminChecked = false;
        return a;
    }

    //통합저장 반영용(빈 값만 채우도록 설계)
    public void enrichIfAbsent(
            String publisher,
            String housingType,
            String supplyType,
            String regionCode,
            String regionName,
            String applyUrl,
            String applyEntryUrl,
            LocalDate startDate,
            LocalDate endDate,
            LocalDate documentPublishedAt,
            LocalDate finalPublishedAt,
            Long rentGtn,
            Long enty,
            Long prtpay,
            Long surlus,
            Long mtRntchrg
    ) {
        // ===== 문자열 =====
        if (isBlank(this.publisher) && !isBlank(publisher)) {
            this.publisher = publisher.trim();
        }
        if (isBlank(this.housingType) && !isBlank(housingType)) {
            this.housingType = housingType.trim();
        }
        if (isBlank(this.supplyType) && !isBlank(supplyType)) {
            this.supplyType = supplyType.trim();
        }
        if (isBlank(this.regionCode) && !isBlank(regionCode)) {
            this.regionCode = regionCode.trim();
        }
        if (isBlank(this.regionName) && !isBlank(regionName)) {
            this.regionName = regionName.trim();
        }
        if (isBlank(this.applyUrl) && !isBlank(applyUrl)) {
            this.applyUrl = applyUrl.trim();
        }
        if (isBlank(this.applyEntryUrl) && !isBlank(applyEntryUrl)) {
            this.applyEntryUrl = applyEntryUrl.trim();
        }

        // ===== 날짜 =====
        if (this.startDate == null && startDate != null) {
            this.startDate = startDate;
        }
        if (this.endDate == null && endDate != null) {
            this.endDate = endDate;
        }
        if (this.documentPublishedAt == null && documentPublishedAt != null) {
            this.documentPublishedAt = documentPublishedAt;
        }
        if (this.finalPublishedAt == null && finalPublishedAt != null) {
            this.finalPublishedAt = finalPublishedAt;
        }

        // ===== 금액 =====
        if (this.rentGtn == null && rentGtn != null) {
            this.rentGtn = rentGtn;
        }
        if (this.enty == null && enty != null) {
            this.enty = enty;
        }
        if (this.prtpay == null && prtpay != null) {
            this.prtpay = prtpay;
        }
        if (this.surlus == null && surlus != null) {
            this.surlus = surlus;
        }
        if (this.mtRntchrg == null && mtRntchrg != null) {
            this.mtRntchrg = mtRntchrg;
        }

    }
    private static boolean isBlank(String s) {
        return s == null || s.isBlank();
    }


}
