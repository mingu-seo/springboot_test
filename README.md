# emotiondiary — Spring Boot 기반 테스트 강의 실습 베이스

Spring Boot 4.0 + JPA + JWT + MariaDB 로 구성된 일기 서비스. **테스트·성능·모니터링·AWS 배포** 를 한 프로젝트에서 모두 다루는 강의 _Spring Boot 기반 테스트_ 의 실습 베이스 프로젝트입니다.

---

## 강의 전체 한눈에 보기

본 강의는 _"코드가 옳게 동작하는가"_ 에서 시작해 _"실제 사용자 트래픽 아래에서도 빠르고 안정적으로 동작하는가"_ 까지, **테스트 → 성능 → 관측성 → 클라우드** 의 4 단계 스토리를 emotiondiary 한 프로젝트로 따라갑니다.

### 강의 흐름

```
[1~4] 정확성 검증              [5~6] 성능 측정              [7~8] 관측성 확보              [9~10] 사용자 체감 + 실전
   테스트 피라미드                k6 로 부하 발생                Prometheus / ELK              Lighthouse → AWS 캡스톤
        │                            │                              │                                │
        └─ "버그 없이 작동" ─────────┴─ "얼마나 빠른가" ───────────┴─ "왜 그 순간 튀었나" ────────┴─ "실 사용자 환경"
```

### 4 개 챕터로 묶어 본 단원 구성

| 챕터                      | 단원          | 한 줄 요약                                                                                                     |
| ------------------------- | ------------- | -------------------------------------------------------------------------------------------------------------- |
| **Ⅰ. 테스트 기반**        | 1 · 2 · 3 · 4 | 단위 → 협력 객체 격리 → Spring 컨텍스트 통합 순으로 테스트 피라미드를 쌓는다                                   |
| **Ⅱ. 성능 측정**          | 5 · 6         | TPS / p95 / SLA 같은 언어를 익히고, k6 시나리오로 emotiondiary 의 SLA 통과 여부를 판정한다                     |
| **Ⅲ. 모니터링**           | 7 · 8         | Prometheus 로 _"언제 / 얼마나"_, ELK 로 _"무엇이 / 왜"_ — 메트릭과 로그가 한 화면에서 만나는 지점까지 구축한다 |
| **Ⅳ. 사용자 체감 + 실전** | 9 · 10        | Lighthouse 로 프론트 체감 속도를, AWS 캡스톤으로 N+1·인덱스·HikariCP·이중화의 Before/After 를 본다             |

### 단원 흐름의 키 메시지

- **1~4 단원** — _"테스트 코드는 변경을 두려워하지 않게 해 주는 안전망"_. JUnit 5 단위 → Mockito 격리 → `@SpringBootTest` / `@WebMvcTest` / `@DataJpaTest` 슬라이스 테스트 순으로 피라미드를 쌓는다.
- **5~6 단원** — _"성능은 숫자다"_. 응답시간 분포(p50/p95/p99), TPS, SLA threshold 라는 언어로 시스템을 평가한다. k6 의 ramping-vus 와 thresholds 로 SLA 통과 여부를 자동 판정.
- **7 단원** — _"점이 아니라 선으로 본다"_. Spring Boot Actuator → Prometheus 15s scrape → Grafana 시계열로 JVM · HTTP p95 · HikariCP · k6 지표를 한 대시보드 위에 겹친다.
- **8 단원** — _"메트릭이 답하지 못하는 질문은 로그에 있다"_. Logback JSON + MDC `traceId` 로 한 요청의 로그 체인을 Kibana KQL 한 줄로 추적. Grafana 의 스파이크 시각 → Kibana 의 traceId 검색의 2단 워크플로우.
- **9 단원** — _"서버 1ms 깎는 것보다 CSS 1개 제거가 LCP 에 더 큰 영향을 줄 때가 많다"_. Lighthouse 12 의 METRICS / INSIGHTS / DIAGNOSTICS 3 단 구조 위에서 Core Web Vitals 를 읽고, WebP · `defer` · `preload` · `server.compression` 으로 Before/After 를 본다.
- **10 단원 (캡스톤)** — _"앞 9 단원이 한 PR 단위에서 함께 작동하는 경험"_. AWS EC2/RDS/ALB 위에서 N+1 → 인덱스 → HikariCP → 이중화 의 4 가지 병목을 한 번에 한 가지씩 고치며 매 단계 k6 + Grafana + Kibana 로 측정.

### 매 단원 누적되는 인프라

```
1~3      :  (이론 / 코드만)
4        :  + H2 (test runtime)
6        :  + MariaDB · k6
7        :  + Prometheus · Grafana · Actuator
8        :  + Elasticsearch · Logstash · Kibana · logback-spring.xml
9        :  + 정적 페이지 (server.compression / cache / SecurityConfig)
10 (캡스톤) :  + AWS EC2 × N · RDS MariaDB · ALB · application-prod.yaml
```

수강생은 단원이 진행될 때마다 `docker compose -f <누적-파일>` 로 스택을 점진적으로 확장합니다 (포트 충돌 없음 — 8080 / 3307 / 9090 / 3000 / 9200 / 5601 / 5044).

### 학습 후 남는 한 장

캡스톤을 통과한 후 머릿속에 남아야 하는 그림은 결국 **측정 → 개선의 닫힌 루프**입니다.

```
코드 변경  →  배포  →  부하 (k6)  →  메트릭 (Grafana) + 로그 (Kibana)  →  다음 병목 결정  →  코드 변경 ...
```

이 루프를 **한 번이라도 직접 돌려본 사람** 이 성능 엔지니어 — 본 강의의 최종 도달점입니다.

---

## 기술 스택

| 영역                         | 스택                                                                                |
| ---------------------------- | ----------------------------------------------------------------------------------- |
| Runtime                      | Java 17 (Amazon Corretto), Spring Boot 4.0.5                                        |
| Persistence                  | Spring Data JPA, MariaDB 10.11, HikariCP                                            |
| Security                     | Spring Security 6, JJWT 0.12                                                        |
| Observability                | Spring Boot Actuator, Micrometer Prometheus, Logback + logstash-logback-encoder 8.0 |
| Build                        | Gradle 8                                                                            |
| Test (개발 진행에 따라 추가) | JUnit 5, Mockito, AssertJ, Spring Test, H2 (slice 테스트)                           |

---

## 도메인 한눈에

```
User ──< Diary
  email/password           id (UUID)
  name                     date (YYYYMMDD)
  role                     content
                           emotionId (1~5)
                           createdAt / updatedAt
```

### 주요 엔드포인트

| Method | Path                                                              | 설명                                                                                      |
| ------ | ----------------------------------------------------------------- | ----------------------------------------------------------------------------------------- |
| POST   | `/api/auth/signup`                                                | 회원가입                                                                                  |
| POST   | `/api/auth/login`                                                 | 로그인 — JSON body 로 `{accessToken, refreshToken, tokenType, accessTokenExpiresIn}` 반환 |
| POST   | `/api/auth/reissue`                                               | 토큰 재발급                                                                               |
| POST   | `/api/auth/logout`                                                | 로그아웃                                                                                  |
| GET    | `/api/diaries?from=YYYYMMDD&to=YYYYMMDD&sort=latest`              | 일기 목록 (응답: `{items, total}`)                                                        |
| GET    | `/api/diaries/{id}`                                               | 일기 단건                                                                                 |
| POST   | `/api/diaries`                                                    | 일기 작성                                                                                 |
| PUT    | `/api/diaries/{id}`                                               | 일기 수정                                                                                 |
| DELETE | `/api/diaries/{id}`                                               | 일기 삭제                                                                                 |
| GET    | `/actuator/health` · `/actuator/prometheus` · `/actuator/metrics` | 관측성 (7단원~)                                                                           |

> ⚠️ `/api/diaries` 의 `from` / `to` 는 **epoch ms 가 아니라 `YYYYMMDD` 정수**(예: `20260423`).
> ⚠️ 로그인 토큰은 **응답 헤더가 아니라 JSON body** 에 있음.

---

## 빠른 시작

```bash
# 1) DB 컨테이너 기동
docker compose -f infra/docker-compose.db.yml up -d

# 2) 앱 실행 (로컬 프로파일)
./gradlew bootRun

# 3) 헬스체크
curl http://localhost:8080/actuator/health
```

기본 포트:

- emotiondiary `:8080`
- MariaDB `:3307` (호스트 publish, 컨테이너 내부는 3306)

---

## 강의 단원별 요약 (1~10)

본 프로젝트는 강의 단원이 진행되며 단계적으로 의존성·설정·인프라가 추가되는 구조입니다. 각 단원의 목표, 베이스 프로젝트에 추가되는 변경, 강의자료 위치를 정리했습니다.

### 단원 한 줄 요약

| #   | 단원                                   | 핵심 키워드                                                                     | 인프라 추가                       |
| --- | -------------------------------------- | ------------------------------------------------------------------------------- | --------------------------------- |
| 1   | 테스트 개요와 개발 패러다임            | 테스트 피라미드 · TDD · 회귀 방지                                               | –                                 |
| 2   | 테스트 프레임워크 (JUnit 5)            | `@Test` · AssertJ · `@ParameterizedTest` · `@Nested`                            | –                                 |
| 3   | 테스트 프레임워크 (Mockito)            | `@Mock` · `@InjectMocks` · `verify` · `ArgumentCaptor`                          | –                                 |
| 4   | Spring Boot 통합 테스트                | `@SpringBootTest` · `@DataJpaTest` · `@WebMvcTest` · MockMvc · TestRestTemplate | H2 (test runtime)                 |
| 5   | 성능테스트 개요                        | TPS · p95 · SLA · k6 / JMeter / nGrinder 비교                                   | –                                 |
| 6   | 성능테스트 실습 (k6)                   | ramping-vus · thresholds · scenarios · checks                                   | k6                                |
| 7   | 메트릭 모니터링 (Prometheus + Grafana) | Pull · histogram_quantile · HikariCP · `${DS_PROMETHEUS}`                       | Prometheus + Grafana              |
| 8   | 로그 모니터링 (ELK)                    | Logback JSON · MDC `traceId` · KQL · Grafana ↔ Kibana                           | Elasticsearch + Logstash + Kibana |
| 9   | 프론트엔드 성능 최적화                 | Lighthouse 12 · Core Web Vitals · WebP · `defer` · `server.compression`         | – (정적 페이지)                   |
| 10  | 캡스톤 — AWS 배포 + 개선 사례          | EC2 · RDS · ALB · N+1 · 인덱스 · HikariCP · 이중화                              | AWS                               |

---

### 1단원 — 테스트 개요와 개발 패러다임

**목표** — 도구 독립적인 이론. 테스트의 정의·역할·테스트 피라미드(Unit / Integration / E2E)·TDD·회귀 방지의 의미.

**프로젝트 변경** — 없음 (이론).

---

### 2단원 — JUnit 5 단위 테스트

**목표** — `@Test` · AssertJ · `@ParameterizedTest` · `@Nested` · `@DisplayName` 으로 클래스 1개를 검증.

**프로젝트 변경**

- `spring-boot-starter-data-jpa-test` · `spring-boot-starter-webmvc-test` 등 모듈별 테스트 starter 가 이미 포함되어 있음 (Spring Boot 4.x 의 분리된 starter 구조)
- 첫 단위 테스트 — 예: `Diary` 엔티티의 `update()` 동작 검증

**실습 방향** — `Diary`, `User` 같은 도메인 객체와 `DiaryService` 의 입력 검증 로직.

---

### 3단원 — Mockito 기반 Mock 테스트

**목표** — Repository · 외부 API 등 협력 객체를 `@Mock` 으로 대체해 Service 내부 로직만 격리 검증.

**프로젝트 변경** — Mockito 는 테스트 starter 에 포함. 추가 의존성 없음.

**실습 방향**

- `DiaryService.create()` 에서 `userRepository.findById()` 와 `diaryRepository.save()` 를 mock
- `BDDMockito.given(...).willReturn(...)` · `verify(...)` · `ArgumentCaptor`
- 예외 경로 검증 — `User not found` 시나리오

---

### 4단원 — Spring Boot 통합 테스트

**목표** — `@SpringBootTest` 전체 컨텍스트, `@WebMvcTest` 컨트롤러 슬라이스, `@DataJpaTest` JPA 슬라이스. MockMvc 로 요청 흐름 검증.

**프로젝트 변경**

- `runtimeOnly 'com.h2database:h2'` 추가 (`@DataJpaTest` 의 in-memory DB)
- `org.springframework.security:spring-security-test` (이미 포함됨)
- 통합 테스트에서 JWT 인증 우회 — `@WithMockUser` 또는 토큰 발급 헬퍼

**실습 방향** — `AuthController` 회원가입/로그인 E2E, `DiaryController` MockMvc 검증, `DiaryRepository` JPA 쿼리 메서드 검증.

---

### 5단원 — 성능테스트 개요

**목표** — TPS · 응답시간 분포(p50/p95/p99) · SLA · 부하/스트레스/스파이크/내구 테스트의 차이. k6 / JMeter / nGrinder 도구 비교.

**프로젝트 변경** — 없음 (이론).

---

### 6단원 — 성능테스트 실습 (k6)

**목표** — `로그인 → 목록 → 작성 → 수정 → 삭제` E2E 시나리오를 k6 로 구현. **VU 10 → 50 → 100** ramp-up + **p95 < 500ms, 에러율 < 1%** threshold 검증.

**프로젝트 변경**

- `infra/docker-compose.db.yml` — MariaDB
- `infra/docker-compose.k6.yml` — k6 컨테이너 (host.docker.internal 로 호스트 Spring Boot 호출)
- `infra/k6/scripts/hello.js` · `diary-list.js` · `diary-scenario.js`
- `infra/mariadb/init/01-schema.sql` — 스키마 시드

**실습 명령**

```bash
docker compose -f infra/docker-compose.db.yml -f infra/docker-compose.k6.yml up -d
docker compose -f infra/docker-compose.k6.yml exec k6 k6 run /scripts/diary-scenario.js
```

---

### 7단원 — 메트릭 모니터링 (Prometheus + Grafana)

**목표** — Spring Boot Actuator `/actuator/prometheus` 를 Prometheus 가 15초마다 스크랩. Grafana 대시보드로 JVM · HTTP p95 · HikariCP · k6 지표를 한 화면에 겹쳐 관측.

**프로젝트 변경**

- `build.gradle` — `spring-boot-starter-actuator` + `micrometer-registry-prometheus`
- `application.yaml` — `management.endpoints.web.exposure.include: health, info, prometheus, metrics` + `metrics.distribution.percentiles-histogram.http.server.requests: true`
- `SecurityConfig` — `/actuator/**` 를 `permitAll` (강의 단계에 한해)
- `infra/prometheus/prometheus.yml` — `host.docker.internal:8080/actuator/prometheus` scrape
- `infra/grafana/provisioning/...` — datasource + dashboard provisioning
- `infra/grafana/dashboards/jvm-micrometer.json` — 커뮤니티 대시보드 ID 4701 (`${DS_PROMETHEUS}` → `Prometheus` 로 치환)
- `infra/docker-compose.monitoring.yml` — Prometheus + Grafana

**핵심 PromQL**

```
sum by (uri) (rate(http_server_requests_seconds_count[1m]))
histogram_quantile(0.95, sum by (le, uri) (rate(http_server_requests_seconds_bucket[5m])))
hikaricp_connections_active
```

---

### 8단원 — 로그 모니터링 (ELK Stack)

**목표** — `Logback → Logstash TCP 5044 → Elasticsearch → Kibana` 파이프라인 구축. **MDC `traceId`** 를 모든 로그에 박아 Kibana KQL 한 줄로 요청 단위 로그 체인 추적.

**프로젝트 변경**

- `build.gradle` — `net.logstash.logback:logstash-logback-encoder:8.0` (Spring Boot 4.x = Logback 1.5.x 호환)
- `src/main/resources/logback-spring.xml` — CONSOLE + LOGSTASH appender, `traceId` MDC 키 JSON 최상위 승격
- `src/main/java/.../common/logging/TraceIdFilter.java` — `OncePerRequestFilter` + `@Order(HIGHEST_PRECEDENCE)`, `X-Trace-Id` 헤더 ↔ MDC 동기화
- `infra/logstash/pipeline/logstash.conf` — input tcp → output elasticsearch
- `infra/docker-compose.logging.yml` — ES + Logstash + Kibana (8.14.x)

**Kibana 활용**

```
KQL: traceId : "abc12345"
KQL: level : ERROR and message : "*N+1*"
```

---

### 9단원 — 프론트엔드 성능 최적화

**목표** — `src/main/resources/static/` 에 데모 페이지를 얹어 **Lighthouse 12** 로 Before/After 측정. Core Web Vitals(LCP / INP / CLS), WebP, `defer`, `preload`, `fetchpriority`, gzip 압축, Cache-Control 적용.

**프로젝트 변경**

- `SecurityConfig` — `/`, `/index.html`, `/css/**`, `/js/**`, `/images/**`, `/favicon.ico` 를 `permitAll`
- `application.yaml`
  ```yaml
  server:
    compression:
      enabled: true
      mime-types: text/html,text/css,text/javascript,application/javascript,application/json,image/svg+xml
      min-response-size: 1KB
  spring:
    web:
      resources:
        cache:
          cachecontrol:
            max-age: 30d
            cache-public: true
        chain:
          strategy:
            content:
              enabled: true
              paths: /**
  ```
- `src/main/resources/static/index.html` + `css/` + `js/app.js` + `js/vendor.js`
  - app.js 는 로그인 토큰을 **JSON body 에서** 꺼내 `Bearer ${accessToken}` 으로 조립
  - vendor.js 는 의도적으로 long task 를 만들어 TBT 시연

**Lighthouse 12 의 3단 구조** — METRICS / INSIGHTS / DIAGNOSTICS. 기존 "Opportunities" 가 `Improve image delivery` · `Render-blocking requests` · `LCP request discovery` 같은 통합 인사이트로 묶임.

---

### 10단원 — [캡스톤] AWS 배포 + 실전 개선 사례

**목표** — 로컬에서 검증한 스택을 **AWS (EC2 + RDS + ALB)** 위에 배포하고, k6 부하 시 만나는 4 가지 병목을 한 번에 한 가지씩 고쳐 Before/After 를 정량 보고.

**Phase A** — 의도적 저사양 환경 구축

- EC2 t3.small × 1, RDS db.t3.micro 단일 AZ, ALB
- `application-prod.yaml` 신설 — RDS 엔드포인트 환경변수, HikariCP pool 5 (작게), Actuator 노출

**Phase B** — Before 부하 (VU 100 / 3분)

- `DiaryResponse.authorName` 추가 → `diary.getUser().getName()` LAZY 트리거 → **N+1 의도적 시연**
- 5만건 시드 SQL 로 의미 있는 응답시간 만들기
- Grafana 4 빨간 신호 (p95, HikariCP active, pending, RDS CPU) + Kibana 에서 traceId 한 개의 user select 51회 확인

**Phase C** — 5 단계 개선 (각 단계마다 k6 재실행)

1. **N+1 제거** — `@EntityGraph(attributePaths = "user")`
2. **인덱스 추가** — `CREATE INDEX idx_diary_user_date ON diary(user_id, date)`
3. **HikariCP 튜닝** — pool 5 → 20, `connection-timeout: 3000`, `leak-detection-threshold`
4. **이중화** — AMI 복제로 EC2 1 → 2 대, ALB target 추가 (JWT stateless 라 가능)
5. **(선택) 캐싱** — Spring Cache + Caffeine, 다중 인스턴스 일관성 경고

**Phase D** — 비교 리포트 + Grafana annotation 으로 시점 표시 + 토론

**비용** — 2~3시간 실습 ~ $0.50, 종료 시 EC2 Terminate / RDS Delete 체크리스트.

---

## 인프라 디렉터리 구조

```
emotiondiary/
├── src/                                # 강의 진행에 따라 점진 추가
│   ├── main/java/com/example/emotiondiary/
│   │   ├── config/SecurityConfig.java
│   │   ├── controller/                 # AuthController · DiaryController
│   │   ├── service/                    # AuthService · DiaryService
│   │   ├── repository/                 # 10단원에서 @EntityGraph 추가
│   │   ├── entity/                     # User · Diary
│   │   ├── dto/                        # 10단원에서 DiaryResponse.authorName 추가
│   │   ├── security/jwt/               # JwtAuthenticationFilter 등
│   │   └── common/logging/             # 8단원: TraceIdFilter
│   └── main/resources/
│       ├── application.yaml            # 7·9단원에서 actuator/compression/cache 보강
│       ├── application-local.yaml
│       ├── application-prod.yaml       # 10단원에서 신설
│       ├── logback-spring.xml          # 8단원에서 신설
│       └── static/                     # 9단원에서 신설 (index.html · css · js)
└── infra/                              # 단원별 docker-compose + provisioning
    ├── docker-compose.db.yml           # 6단원: MariaDB
    ├── docker-compose.k6.yml           # 6단원: k6
    ├── docker-compose.monitoring.yml   # 7단원: Prometheus + Grafana
    ├── docker-compose.logging.yml      # 8단원: Elasticsearch + Logstash + Kibana
    ├── mariadb/init/01-schema.sql
    ├── k6/scripts/                     # hello.js · diary-list.js · diary-scenario.js
    ├── prometheus/prometheus.yml
    ├── grafana/
    │   ├── provisioning/datasources/prometheus.yml
    │   ├── provisioning/dashboards/dashboards.yml
    │   └── dashboards/jvm-micrometer.json
    └── logstash/pipeline/logstash.conf
```

### 단원별 compose 누적 실행 예시

```bash
# 2~4단원 — DB 만
docker compose -f infra/docker-compose.db.yml up -d

# 6단원 — DB + k6
docker compose -f infra/docker-compose.db.yml -f infra/docker-compose.k6.yml up -d

# 7단원 — + Prometheus + Grafana
docker compose -f infra/docker-compose.db.yml \
               -f infra/docker-compose.k6.yml \
               -f infra/docker-compose.monitoring.yml up -d

# 8단원 — + ELK
docker compose -f infra/docker-compose.db.yml \
               -f infra/docker-compose.k6.yml \
               -f infra/docker-compose.monitoring.yml \
               -f infra/docker-compose.logging.yml up -d
```

포트는 충돌 없이 공존: 8080 (app) · 3307 (DB) · 9090 (Prometheus) · 3000 (Grafana) · 9200 (ES) · 5601 (Kibana) · 5044 (Logstash).

---

## 강의자료 위치

본 README 가 있는 저장소 외부 (강의자료 폴더):

- `Spring Boot 기반 테스트/01. 테스트 개요와 개발 패러다임.md`
- `Spring Boot 기반 테스트/02. 테스트 프레임워크 (JUnit 5).md`
- `Spring Boot 기반 테스트/03. 테스트 프레임워크 (Mockito).md`
- `Spring Boot 기반 테스트/04. Spring Boot 통합 테스트.md`
- `Spring Boot 기반 테스트/05. 성능테스트 개요.md`
- `Spring Boot 기반 테스트/06. 성능테스트 실습 (k6).md`
- `Spring Boot 기반 테스트/07. 메트릭 모니터링 (Prometheus + Grafana).md`
- `Spring Boot 기반 테스트/08. 로그 모니터링 (ELK).md`
- `Spring Boot 기반 테스트/09. 프론트엔드 성능 최적화.md`
- `Spring Boot 기반 테스트/10. [캡스톤] AWS 배포와 실전 개선 사례.md`

---

## 데모 계정 (시드 가정)

```
email:    demo@test.com
password: pw12345!
```

```bash
# 첫 가입
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@test.com","password":"pw12345!","name":"Demo"}'

# 로그인
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"demo@test.com","password":"pw12345!"}'
```

---

## 라이선스 / 용도

본 프로젝트는 **강의 실습용 베이스**입니다. 운영 배포 전에는 다음 항목을 반드시 점검하세요.

- `SecurityConfig` 의 `/actuator/**` `permitAll` 은 **운영 환경에서 제거**하거나 별도 포트/인증으로 분리
- `application-local.yaml` 의 DB 비밀번호, JWT secret 같은 평문 설정은 **시크릿 매니저로 이전**
- `spring.jpa.hibernate.ddl-auto: update` 는 **운영에서 사용 금지** (Phase A `application-prod.yaml` 은 `validate`)
- `/api/auth/login` 응답에 토큰을 body 로 내려주는 현 구조는 강의용. 운영에선 **HttpOnly Secure 쿠키 + SameSite=Strict** 또는 OAuth2 Resource Server 권장
