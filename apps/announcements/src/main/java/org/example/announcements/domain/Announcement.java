package org.example.announcements.domain;

import jakarta.persistence.*;
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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AnnouncementSource source; // 출처(MYHOME/SH_RSS)

    @Column(name = "external_key", nullable = false) // 유니크 키로 사용
    private String externalKey; // 외부식별자 (예: MYHOME= pblancId:houseSn, SH=seq)

    @Column
    private String title; // 공고명

    @Column(name = "publisher", nullable = false)
    private String publisher; // 발행처

    @Column(name = "housing_type")
    private String housingType; // 주택 유형

    @Column(name = "supply_type")
    private String supplyType; // 공급유형

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


    //-----------생성 수정 시간---------
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false) //  수정 불가
    private LocalDateTime createdAt; // 생성 시각

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt; // 수정 시각


}
