package org.example.announcements.repository;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.example.announcements.dto.applicationmanage.ApplicationManageAnnouncementRow;
import org.example.announcements.dto.applicationmanage.ApplicationManageSummaryCounts;

import java.time.LocalDate;
import java.util.List;

import static org.example.announcements.domain.QAnnouncement.announcement;
import static org.example.announcements.domain.QAnnouncementApplication.announcementApplication;


@RequiredArgsConstructor
public class AnnouncementApplicationManageQueryRepositoryImpl implements AnnouncementApplicationManageQueryRepository {

    private final JPAQueryFactory jpaQueryFactory;


    //지원중 목록 조회
    @Override
    public List<ApplicationManageAnnouncementRow> findApplyingRows(String userId, LocalDate today, Long cursor, int sizePlusOne) {

        var q = jpaQueryFactory.select(
                        Projections.constructor(
                                ApplicationManageAnnouncementRow.class,
                                announcement.id,
                                announcement.title,
                                announcement.publisher,
                                announcement.housingType,
                                announcement.endDate,
                                announcement.documentPublishedAt,
                                announcement.finalPublishedAt
                        )
                )
                .from(announcementApplication) // 유저의 지원완료 기록테이블
                .join(announcement)
                .on(announcement.id.eq(announcementApplication.announcementId))
                .where(
                        announcementApplication.userId.eq(userId),
                        announcement.endDate.goe(today), // applying조건
                        announcement.documentPublishedAt.isNull() // 서류발표일이없으면 통과
                                .or(announcement.documentPublishedAt.gt(today))
                );

        //커서
        if (cursor != null) { // cursor가 null이면 최초 페이지로 간주
            q.where(announcement.id.lt(cursor));
        }

        //announcementId desc로 정렬하고 다음페이지 존재여부확인
        return  q.orderBy(announcement.id.desc())
                .limit(sizePlusOne)
                .fetch();

    }

    // 서류 발표대기
    @Override
    public List<ApplicationManageAnnouncementRow> findDocumentWaitingRows(String userId, LocalDate today, Long cursor, int sizePlusOne) {
        var q = jpaQueryFactory.select(
                        Projections.constructor(
                                ApplicationManageAnnouncementRow.class,
                                announcement.id,
                                announcement.title,
                                announcement.publisher,
                                announcement.housingType,
                                announcement.endDate,
                                announcement.documentPublishedAt,
                                announcement.finalPublishedAt
                        )
                )
                .from(announcementApplication)
                .join(announcement) // 공고 테이블이랑 조인
                .on(announcement.id.eq(announcementApplication.announcementId))
                .where(
                        announcementApplication.userId.eq(userId),
                        announcement.endDate.lt(today), // endDate < today
                        announcement.documentPublishedAt.isNotNull(), // documentPublishedAt은 있어야 함
                        announcement.documentPublishedAt.gt(today) // today < documentPublishedAt
                );

        if (cursor != null) {
            q.where(announcement.id.lt(cursor));
        }

        return q.orderBy(announcement.id.desc())
                .limit(sizePlusOne)
                .fetch();
    }

    //최종발표 대기
    @Override
    public List<ApplicationManageAnnouncementRow> findFinalWaitingRows(String userId, LocalDate today, Long cursor, int sizePlusOne) {
        var q = jpaQueryFactory.select(
                        Projections.constructor(
                                ApplicationManageAnnouncementRow.class,
                                announcement.id,
                                announcement.title,
                                announcement.publisher,
                                announcement.housingType,
                                announcement.endDate,
                                announcement.documentPublishedAt,
                                announcement.finalPublishedAt
                        )
                )
                .from(announcementApplication)
                .join(announcement)
                .on(announcement.id.eq(announcementApplication.announcementId))
                .where(
                        announcementApplication.userId.eq(userId),
                        announcement.documentPublishedAt.isNotNull(), // documentPublishedAt은 있어야 함
                        announcement.documentPublishedAt.loe(today), // documentPublishedAt <= today
                        announcement.finalPublishedAt.isNotNull(), // finalPublishedAt은 있어야 함
                        announcement.finalPublishedAt.gt(today) // today < finalPublishedAt
                );

        if (cursor != null) {
            q.where(announcement.id.lt(cursor));
        }

        return q.orderBy(announcement.id.desc())
                .limit(sizePlusOne)
                .fetch();
    }


    //발표완료 조회
    @Override
    public List<ApplicationManageAnnouncementRow> findClosedRows(String userId, LocalDate today, Long cursor, int sizePlusOne) {
        var q = jpaQueryFactory.select(
                        Projections.constructor(
                                ApplicationManageAnnouncementRow.class,
                                announcement.id,
                                announcement.title,
                                announcement.publisher,
                                announcement.housingType,
                                announcement.endDate,
                                announcement.documentPublishedAt,
                                announcement.finalPublishedAt
                        )
                )
                .from(announcementApplication)
                .join(announcement)
                .on(announcement.id.eq(announcementApplication.announcementId))
                .where(
                        announcementApplication.userId.eq(userId),
                        announcement.finalPublishedAt.isNotNull(), // 최종 발표일이 있어야 발표 완료로 볼 수 있음
                        announcement.finalPublishedAt.loe(today) // finalPublishedAt <= today
                );
        if (cursor != null) {
            q.where(announcement.id.lt(cursor));
        }

        return q.orderBy(announcement.id.desc())
                .limit(sizePlusOne)
                .fetch();
    }

    // 상단 서머리 카운트 조회
    @Override
    public ApplicationManageSummaryCounts countSummary(String userId, LocalDate today) {
        // 상태별 카운트
        //APPLYING용
        NumberExpression<Integer> applyingExpr =
                new CaseBuilder()
                        .when(
                                announcement.endDate.goe(today)
                                        .and(
                                                announcement.documentPublishedAt.isNull()
                                                        .or(announcement.documentPublishedAt.gt(today))
                                        )
                        )
                        .then(1)
                        .otherwise(0);

        //DOCUMENT_WAITING 용
        NumberExpression<Integer> documentWaitingExpr =
                new CaseBuilder()
                        .when(
                                announcement.endDate.lt(today)
                                        .and(announcement.documentPublishedAt.isNotNull())
                                        .and(announcement.documentPublishedAt.gt(today))
                        )
                        .then(1)
                        .otherwise(0);

        //FiNAL_WAITING용
        NumberExpression<Integer> finalWaitingExpr =
                new CaseBuilder()
                        .when(
                                announcement.documentPublishedAt.isNotNull()
                                        .and(announcement.documentPublishedAt.loe(today)) // documentPublishedAt <= today
                                        .and(announcement.finalPublishedAt.isNotNull())
                                        .and(announcement.finalPublishedAt.gt(today)) // today < finalPublishedAt
                        )
                        .then(1) // 조건 만족하면 1
                        .otherwise(0);

        //합계시작
        NumberExpression<Integer> applyingSum =
                Expressions.numberTemplate(Integer.class, "sum({0})", applyingExpr).coalesce(0);

        NumberExpression<Integer> documentWaitingSum =
                Expressions.numberTemplate(Integer.class, "sum({0})", documentWaitingExpr).coalesce(0);

        NumberExpression<Integer> finalWaitingSum =
                Expressions.numberTemplate(Integer.class, "sum({0})", finalWaitingExpr).coalesce(0);


        //실행결과를 튜플로 받기
        Tuple tuple = jpaQueryFactory
                .select(applyingSum, documentWaitingSum, finalWaitingSum)
                .from(announcementApplication)
                .join(announcement).on(announcement.id.eq(announcementApplication.announcementId))
                //로그인 유저의 지원완료 기록만 대상으로 제한하기
                .where(announcementApplication.userId.eq(userId))
                .fetchOne();

        if (tuple == null) {
            return ApplicationManageSummaryCounts.empty();
        }

        Integer applying = tuple.get(applyingSum);
        Integer documentWaiting = tuple.get(documentWaitingSum);
        Integer finalWaiting = tuple.get(finalWaitingSum);

        return new ApplicationManageSummaryCounts(
                applying == null ? 0 : applying.longValue(),
                documentWaiting == null ? 0 : documentWaiting.longValue(),
                finalWaiting == null ? 0 : finalWaiting.longValue()
        );
    }
}
