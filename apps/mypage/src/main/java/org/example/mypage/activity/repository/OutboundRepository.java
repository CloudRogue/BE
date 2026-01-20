package org.example.mypage.activity.repository;

import org.example.mypage.activity.domain.Outbound;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OutboundRepository extends JpaRepository<Outbound, Long> {

    /**
     * Outbound(최근 접속 공고 히스토리) 조회/페이징용 Repository.
     *
     * <p>scrollByUserId:
     * <ul>
     *   <li>사용자별(userId) Outbound를 id DESC로 커서 스크롤 조회합니다.</li>
     *   <li>cursor가 있으면 {@code o.id < cursor} 조건으로 다음 페이지를 가져옵니다.</li>
     *   <li>{@code Pageable} 크기를 {@code size+1}로 주면 hasNext 판단에 사용할 수 있습니다.</li>
     * </ul>
     */
    @Query("""
        select o
        from Outbound o
        where o.userId = :userId
          and (:cursor is null or o.id < :cursor)
        order by o.id desc
    """)
    List<Outbound> scrollByUserId(@Param("userId") String userId,
                                  @Param("cursor") Long cursor,
                                  Pageable pageable);

}
