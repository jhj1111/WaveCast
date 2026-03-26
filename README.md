# 🎙️ WaveCast

> 별도의 백엔드 서버 없이 공개 RSS 피드와 Podcast Index API만으로  
> 팟캐스트를 탐색·재생·구독할 수 있는 오픈소스 안드로이드 앱입니다.

| 홈 피드 | 플레이어 | 라이브러리 |
|:---:|:---:|:---:|
| ![home](https://github.com/user-attachments/assets/fa86dbf8-f334-4386-88a0-f5b28f73dde5) | ![player](https://github.com/user-attachments/assets/3af51133-fe09-4344-9b70-bab50ef154bf) | ![library](https://github.com/user-attachments/assets/b62ebbbb-fc91-4e03-b78e-066cb10a56a0) |

<br>

## ✨ 주요 기능

| 기능 | 설명 |
|---|---|
| 🔍 탐색 및 검색 | Podcast Index API 기반 장르·키워드 검색, Paging 3 무한 스크롤 |
| ▶️ 에피소드 재생 | ExoPlayer(Media3) 기반 오디오 스트리밍 |
| 🔔 백그라운드 제어 | MediaSession + Foreground Service로 알림바·잠금화면 미디어 컨트롤 |
| 📥 구독 및 오프라인 재생 | Room DB 구독 저장, 에피소드 다운로드 후 오프라인 재생 지원 |

<br>

## 🏗️ 아키텍처

### 설계 원칙

**Clean Architecture + MVI** 패턴을 기반으로 설계했으며, 두 가지 핵심 목표를 가지고 구조를 잡았습니다.

- **관심사 분리** — UI, 도메인, 데이터 계층을 명확히 분리해 각 계층이 단일 책임을 갖도록 구성했습니다. ViewModel은 UI 상태(UiState)만 관리하고, 비즈니스 로직은 Repository 계층이 담당합니다.
- **독립적 개발 및 빌드 속도** — 기능 단위로 모듈을 분리하는 **멀티 모듈 구조**를 채택했습니다. 변경된 모듈만 재빌드되므로 빌드 캐시 활용도를 높이고, 팀 내 기능 병렬 개발을 용이하게 합니다.

### 멀티 모듈 구조

```
WaveCast/
├── app/                   # 진입점, Navigation Graph, DI 조합
├── benchmark/             # Macrobenchmark 성능 측정 및 Baseline Profile
├── build-logic/           # Convention Plugin 기반 공통 빌드 설정
├── core/
│   ├── data/              # Repository, PagingSource
│   ├── database/          # Room DAO, Entity
│   ├── media/             # ExoPlayer 래핑, MediaSessionService
│   ├── network/           # Retrofit, RSS 파서, Podcast Index API 클라이언트
│   └── ui/                # 공통 Compose 컴포넌트, 디자인 토큰
└── feature/
    ├── home/              # 탐색·검색 UI
    ├── library/           # 구독·다운로드·재생 기록
    └── player/            # 재생 화면 및 MiniPlayer
```

| 모듈 | 역할 |
|---|---|
| `:app` | Navigation Graph 조합, Hilt DI 설정 |
| `:feature:*` | 화면 단위 독립 UI 계층 — ViewModel, UiState, Compose Screen |
| `:core:data` | Repository 패턴으로 네트워크·DB 데이터 통합 제공 |
| `:core:network` | Retrofit + OkHttp 기반 API 통신, RSS XML 파싱 |
| `:core:database` | Room Entity·DAO 정의, 마이그레이션 관리 |
| `:core:media` | ExoPlayer 싱글턴 래핑, MediaSessionService 생명주기 관리 |
| `:core:ui` | 재사용 가능한 Composable 컴포넌트, Material3 테마 확장 |

### 데이터 흐름 (MVI)

```
User Action
    │
    ▼
[Feature ViewModel]  ── Intent 처리 ──▶  [Repository]
    │                                        │
    │  UiState (StateFlow)           Network / Room / Cache
    │                                        │
    ▼                                        ▼
[Compose Screen]  ◀────────────── 데이터 응답 (Result<T>)
```

- **단방향 데이터 흐름** — UI는 Intent를 전달하고, ViewModel이 UiState를 갱신합니다.
- **Result 패턴** — Repository 계층에서 `Result<T>`로 성공/실패를 래핑해 ViewModel까지 일관되게 전달합니다.

<br>

## 🛠️ 기술 스택

| 분류 | 사용 기술 |
|---|---|
| Language | Kotlin 100% |
| UI | Jetpack Compose, Material3, Navigation Compose |
| 비동기 | Coroutines, StateFlow, SharedFlow, Paging 3 |
| 미디어 | Media3 (ExoPlayer), MediaSessionService, Foreground Service |
| 네트워크 / DB | Retrofit2, OkHttp3, Room 2.x, DataStore Preferences |
| DI / 이미지 | Hilt, Coil 3 |

<br>

## 🚀 성능 최적화

Macrobenchmark를 활용해 성능 지표를 정량적으로 측정하고 개선했습니다.

### 1. 스크롤 성능 — 최악 프레임 지연 38% 개선

| 지표 | 최적화 전 | 최적화 후 | 개선율 |
|:---:|:---:|:---:|:---:|
| P95 프레임 지연 시간 | 314.9ms | 193.1ms | **▼ 38%** |

**적용 기법:**
- `LazyColumn` 아이템에 안정적인 `key` 파라미터를 지정해 불필요한 재구성(Recomposition) 방지
- 아이템 내부 무거운 연산을 `remember`로 캐싱하여 스크롤 중 연산 최소화

### 2. 초기 실행 속도 — Cold Start 52% 단축

| 지표 | 최적화 전 | 최적화 후 | 개선율 |
|:---:|:---:|:---:|:---:|
| Cold Start (중앙값) | 2,351.4ms | 1,120.5ms | **▼ 52%** |

**적용 기법:**
- **Baseline Profile** 적용으로 자주 실행되는 코드 경로를 AOT 컴파일 대상에 포함
- 앱 실행 초기 Hilt 그래프 초기화 비용을 줄이기 위해 지연 주입(Lazy Injection) 활용

### 3. 오프라인 대응 및 에러 핸들링

- `ConnectivityManager`로 네트워크 상태를 실시간 모니터링하고 Flow로 노출
- 네트워크 오류 시 로컬 캐시를 우선 노출하는 **Graceful Degradation** 전략 적용
- `Result<T>` 래퍼 패턴으로 Repository → ViewModel → UI 전 계층에 걸쳐 일관된 에러 처리 및 UI 피드백 제공

<br>

## 🧪 테스트 전략

품질 중심 개발을 위해 **4계층 테스트 전략**을 수립하여 적용했습니다.

| 계층 | 도구 | 검증 대상 |
|---|---|---|
| Unit Test | JUnit5, Mockk, Turbine | ViewModel 상태 전환, Repository 캐싱 로직, Flow 방출 검증 |
| Integration Test | AndroidX Test | Room DAO CRUD, WorkManager 다운로드 작업 통합 동작 |
| UI Test | ComposeTestRule, Espresso | 재생 화면 인터랙션, 검색·구독 E2E 플로우 |
| Macrobenchmark | Benchmark 라이브러리 | Cold Start 시간, 피드 스크롤 프레임 드롭 |