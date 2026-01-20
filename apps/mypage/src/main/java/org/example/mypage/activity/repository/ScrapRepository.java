package org.example.mypage.activity.repository;

import org.example.mypage.activity.domain.Scrap;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScrapRepository extends JpaRepository<Scrap, Long> {


    /**
     * Scrap(즐겨찾기) 조회/삭제/중복검사용 Repository.
     *
     * <ul>
     *   <li>scrollByUserId: userId 기준으로 id DESC 커서 스크롤 조회.
     *       cursor가 있으면 {@code s.id < cursor} 조건으로 다음 페이지를 가져옵니다.
     *       {@code Pageable}을 {@code size+1}로 주면 hasNext 판단에 사용할 수 있습니다.</li>
     *   <li>deleteByUserIdAndAnnouncementId: (userId, announcementId) 스크랩을 삭제하고, 삭제된 행 수를 반환합니다.</li>
     *   <li>existsByUserIdAndAnnouncementId: (userId, announcementId) 스크랩 존재 여부를 반환합니다.</li>
     * </ul>
     */
    @Query("""
        select s
        from Scrap s
        where s.userId = :userId
          and (:cursor is null or s.id < :cursor)
        order by s.id desc
    """)
    List<Scrap> scrollByUserId(@Param("userId") String userId, @Param("cursor") Long cursor, Pageable pageable
    );

    int deleteByUserIdAndAnnouncementId(String userId, Long announcementId);
    boolean existsByUserIdAndAnnouncementId(String userId, Long announcementId);
}
