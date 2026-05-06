## Team

| 이름 | 역할 | 주요 담당 |  
| --- | --- | --- | 
| 안지철 | BE 리더 | 지원 자격 시스템 개발, 사용자 프로필 개발, 어드민 파이프라인 구축, AI 기반 공고 요약 및 지원 자격 진단 개발 |  
| 안희건 | BE | Oauth2 + JWT 기반 로그인 구현, Infra 구축, 댓글 시스템 개발|  
| 신수호 | BE | LH 및 SH 파싱 시스템 개발, 공고 도메인 개발, 공고 API 개발 |  

<br><br>

## Tech Stack

- Backend: Spring Boot, Java 21, Spring Data JPA
- DB: MySQL, PostgreSQL, Redis
- Infra: AWS ECS, ECR, RDS, Nginx

<br><br>

## 아키텍처 & ERD

<img width="920" height="639" alt="image" src="https://github.com/user-attachments/assets/27c689fe-c392-4ab9-b173-0a9ba3d78ecc" />

<br><br>

<img width="2250" height="2224" alt="Untitled" src="https://github.com/user-attachments/assets/51558a79-c5b6-4635-bd11-e72f9f2486c6" />

<br><br>


## 🧱 패키지/모듈 구조

```text
org.example
  ├─ admin                         // 담당자: 안지철  관리자 서비스: 공고 등록/보강, AI API 연동, 마이페이지 온보딩 관리 연동
  │  ├─ AdminApplication.java
  │  ├─ advice                     // 전역 예외 처리
  │  ├─ api                        // 외부/내부 API 클라이언트 및 매퍼
  │  ├─ config                     // RestClient, ObjectMapper 설정
  │  ├─ controller                 // 관리자 API 컨트롤러
  │  ├─ dto                        // 관리자 요청/응답 DTO
  │  │  ├─ request
  │  │  └─ response
  │  ├─ exception                  // 관리자 도메인 예외
  │  └─ service                    // 관리자 비즈니스 로직
  │
  ├─ announcements                 // 담당자: 신수호  공고 도메인: 공고 목록/상세/검색, 지원, 개인화 추천, 공고 수집
  │  ├─ AnnouncementsApplication.java
  │  ├─ api                        // 목록 응답, 커서, 정렬 등 API 모델
  │  ├─ config                     // Querydsl, 외부 서비스 RestClient 설정
  │  ├─ controller                 // 공고 조회/검색/필터/지원 관리 컨트롤러
  │  │  └─ internal                // 내부 공고 수집 API
  │  ├─ domain                     // 공고, 요약, 지역, 문서, 지원 엔티티
  │  ├─ dto                        // 공고 관련 DTO
  │  │  ├─ applicationmanage       // 지원 관리 DTO
  │  │  ├─ filters                 // 필터 응답 DTO
  │  │  ├─ ingest                  // 공고 수집 요청 DTO
  │  │  └─ internal
  │  │     └─ mypage               // 마이페이지 연동 DTO
  │  ├─ exception                  // 공고 도메인 예외/에러 응답
  │  ├─ listener                   // 공고 수집/이벤트 리스너
  │  ├─ port                       // 관리자 공고 보강 유스케이스/포트
  │  ├─ redis                      // 수집 중복 방지 Redis 어댑터
  │  ├─ repository                 // 공고 JPA/Querydsl Repository
  │  ├─ service                    // 공고 조회/검색/지원/개인화 서비스
  │  │  ├─ ingest                  // 공고 수집 서비스
  │  │  └─ internal                // 내부 서비스 클라이언트
  │  │     ├─ ai                   // AI 자격진단 클라이언트
  │  │     └─ mypage               // 마이페이지 클라이언트
  │  └─ util                       // 커서, 상태 계산 유틸
  │
  ├─ auth                          // 담당자: 안희건  인증/인가: OAuth2 로그인, JWT 발급/검증, 사용자 식별
  │  ├─ AuthApplication.java
  │  ├─ api                        // 인증 내부 API 클라이언트
  │  ├─ config                     // Spring Security, JWT, RestClient 설정
  │  ├─ controller                 // 인증/헬스/JWKS/개발용 API
  │  ├─ domain                     // 사용자, OAuth 제공자, 권한 엔티티/Enum
  │  ├─ dto                        // Principal, 프로필 생성 요청, 에러 응답
  │  ├─ exception                  // 인증 실패/권한 거부 핸들러
  │  ├─ repository                 // 사용자 Repository
  │  └─ service                    // OAuth2 사용자 처리, JWT, 로그인 성공 핸들러
  │
  ├─ core                          // 모듈 베이스
  │  ├─ CoreApplication.java
  │  └─ community                  // 커뮤니티 댓글/좋아요/신고/필터링
  │     ├─ controller
  │     ├─ domain
  │     │  └─ enums
  │     ├─ dto
  │     │  ├─ request
  │     │  └─ response
  │     ├─ exception
  │     ├─ repository
  │     └─ service
  │
  ├─ mypage                        // 담당자: 안지철 마이페이지: 프로필/온보딩, 스크랩/발송 이력, 알림 설정
  │  ├─ MypageApplication.java
  │  ├─ activity                   // 사용자 활동: 스크랩, 아웃바운드 이력
  │  │  ├─ api                     // 공고 서비스 연동 API
  │  │  ├─ controller
  │  │  ├─ domain
  │  │  ├─ dto
  │  │  │  └─ response
  │  │  ├─ repository
  │  │  └─ service
  │  ├─ advice                     // 전역 예외 처리
  │  ├─ config                     // Security, JWT Resource Server, Jackson 설정
  │  ├─ exception                  // 마이페이지 도메인 예외
  │  │  └─ enums
  │  ├─ notify                     // 알림 설정: 이메일/카카오/리마인더
  │  │  ├─ controller
  │  │  ├─ domain
  │  │  ├─ dto
  │  │  ├─ repository
  │  │  └─ service
  │  ├─ profile                    // 프로필/온보딩/자격 조건
  │  │  ├─ controller
  │  │  ├─ domain
  │  │  │  └─ enums
  │  │  ├─ dto
  │  │  │  ├─ request
  │  │  │  └─ response
  │  │  ├─ repository
  │  │  └─ service
  │  ├─ util                       // JSON 변환, 지역 코드북 
  └─         
```
<br><br>
