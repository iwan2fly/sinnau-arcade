# 🕹️ Sinnau Arcade (신나유 오락실) - Development Guidelines

이 파일은 Sinnau Arcade 프로젝트의 아키텍처, 코딩 컨벤션 및 개발 흐름을 정의합니다. 모든 AI 에이전트 및 개발자는 이 가이드를 최우선으로 준수해야 합니다.

---

## 🏗️ Project Architecture

프로젝트는 **Full-stack Monorepo** 구조를 따르며, 백엔드와 프론트엔드가 명확히 분리되어 있습니다.

- `/backend`: Spring Boot 3.4 (Java 21) 기반 API 서버
- `/frontend`: React 19 (Vite) 기반 SPA

---

## 🟢 Backend Conventions (Spring Boot)

### 1. Technology Stack
- **Framework:** Spring Boot 3.4.1
- **Language:** Java 21 (Record, Pattern Matching 등 최신 기능 적극 활용)
- **Data Access:** Spring Data JDBC (JPA가 아니므로 @Entity 대신 @Table 사용 및 명시적 매핑 선호)
- **Security:** Spring Security + JWT (jjwt 라이브러리 사용)
- **Caching:** Caffeine Cache (Refresh Token 저장 등)

### 2. Package Structure
- `kr.sinnau.platform.api.local.v1`: 외부 노출 API 컨트롤러 및 Request/Response DTO
- `kr.sinnau.platform.domain.[domain]`: 도메인별 Entity, Repository, Service 로직
- `kr.sinnau.platform.common`: 공통 예외 처리, 보안 설정, AOP(EventLog)

### 3. Implementation Rules
- **DTO 사용:** 컨트롤러와 서비스 간 데이터 전달 시 반드시 DTO(특히 Java 21 `record`)를 사용합니다.
- **예외 처리:** `SinnauException`과 `SinnauErrorCode`를 사용하여 중앙 집중식으로 관리합니다.
- **Lombok:** `@Getter`, `@RequiredArgsConstructor`, `@Builder` 등을 활용하여 보일러플레이트를 최소화합니다.
- **Logging:** 중요한 비즈니스 로직에는 `@EventLog` 애노테이션을 사용하여 AOP 기반 로깅을 수행합니다.

---

## 🔵 Frontend Conventions (React)

### 1. Technology Stack
- **Library:** React 19
- **Build Tool:** Vite
- **Routing:** React Router Dom v7
- **HTTP Client:** Axios (중앙 집중식 설정: `src/api/axios.js`)

### 2. Implementation Rules
- **Component:** 함수형 컴포넌트와 Hooks를 기본으로 사용합니다.
- **Styling:** Vanilla CSS(`App.css`, `Game.css`)를 사용하여 직관적인 스타일링을 유지합니다.
- **API Call:** 모든 API 호출은 `src/api/axios.js`에 설정된 인스턴스를 통해 수행하며, 비동기 처리는 `async/await`를 사용합니다.
- **State Management:** 가급적 React 내장 상태(useState, useContext)를 활용하되, 복잡한 전역 상태가 필요한 경우에만 추가 라이브러리를 고려합니다.

---

## 🛠️ Workflow & Commands

### Backend (in `/backend` directory)
- Build: `./gradlew build`
- Run: `./gradlew bootRun`
- Test: `./gradlew test`

### Frontend (in `/frontend` directory)
- Install: `npm install`
- Dev: `npm run dev`
- Build: `npm run build`

---

## 🤖 AI Interaction Focus
- **Surgical Updates:** 코드 수정 시 기존 스타일(Lombok, Record 사용 등)을 엄격히 준수하세요.
- **Security First:** JWT 토큰 처리나 인증 로직 수정 시 `SecurityConfig`와의 호환성을 반드시 확인하세요.
- **Verification:** 변경 사항 적용 후 반드시 관련 테스트(JUnit)나 빌드 성공 여부를 체크하세요.
