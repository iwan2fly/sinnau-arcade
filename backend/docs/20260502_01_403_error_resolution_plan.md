# Fix 403 Forbidden on `/game` Endpoint

## 문제 원인 분석 (Overall Structure)

현재 프로젝트는 **Thymeleaf(SSR) + Vue.js(CSR) + JWT(Stateless)** 구조를 띄고 있습니다.
사용자님께서 메뉴에서 `<a href="/game">게임</a>`을 클릭했을 때 403이 발생한 원인은 크게 두 가지입니다.

1. **인증 아키텍처 불일치 (403 원인):** 
   로그인 시 발급된 `accessToken`을 브라우저의 `localStorage`에 저장하고 계십니다. 하지만 브라우저의 일반적인 페이지 이동(a 태그 클릭) 시에는 `localStorage`의 값이 HTTP `Authorization` 헤더에 자동으로 담기지 않습니다. 따라서 스프링 시큐리티 필터는 토큰이 없는 "비로그인" 요청으로 간주하여 403(권한 없음)으로 튕겨냅니다.
2. **컨트롤러 부재 (잠재적 404 원인):**
   현재 `WebMainController`와 `WebGateController`만 존재하며, `/game` 경로에 매핑된 스프링 컨트롤러와 해당 화면을 그려줄 `game.html` 템플릿이 없습니다. (403 에러가 먼저 터져서 404까지 도달하지 않은 상태입니다.)

---

## User Review Required

Thymeleaf와 JWT를 함께 사용할 때 가장 권장되는 두 가지 해결 아키텍처를 제안합니다. 어느 방향으로 진행할지 선택해 주시면 그에 맞춰 구현하겠습니다.

> [!IMPORTANT]
> **옵션 A: 화면(View)은 모두 열어두고, 데이터(API)만 잠그기 (현재 구조와 가장 잘 맞음)**
> - **방식:** `/game` 같은 화면 진입 URL은 `SecurityConstants`의 `AUTH_WHITELIST`에 추가하여 누구나 화면(HTML) 파일은 받을 수 있게 둡니다.
> - **인증:** 화면이 로드된 직후, Vue.js가 `localStorage`에 있는 `accessToken`을 사용해 백엔드 API 데이터를 가져오거나 검증합니다. 만약 로컬에 토큰이 없으면 Vue.js가 알아서 로그인 페이지(`/gate/playGate`)로 튕겨냅니다.
> - **장점:** 지금처럼 `localStorage`에 토큰을 저장하는 방식을 그대로 유지할 수 있으며 프론트엔드 중심의 유연한 개발이 가능합니다.

> [!NOTE]
> **옵션 B: AccessToken을 쿠키(Cookie)로 구워서 처리하기**
> - **방식:** 로그인(`ApiAuthController`) 시 JSON 바디뿐만 아니라 HttpOnly 쿠키에도 `accessToken`을 담아 응답합니다. `JwtAuthenticationFilter`도 쿠키에서 토큰을 읽도록 수정합니다.
> - **인증:** 브라우저가 화면을 이동(`<a href="/game">`)할 때마다 쿠키가 자동으로 백엔드에 전송되므로, 스프링 시큐리티가 사용자를 인식하고 정상적으로 화면을 렌더링해 줍니다.
> - **장점:** 전통적인 SSR 방식(JSP, Thymeleaf)에 아주 적합하고 보안상(XSS 방지) 유리합니다.

---

## Proposed Changes

어떤 옵션을 선택하시든 공통적으로 다음 작업이 필요합니다.

### 1. Game 화면 추가 (공통)
- **[NEW]** `WebGameController.java`: `/game` 경로를 매핑하여 `game.html` 반환
- **[NEW]** `game.html`: 기본 레이아웃(header 포함)이 적용된 게임 메인 화면 구성

### 2. 옵션 A 선택 시
- **[MODIFY]** `SecurityConstants.java`: `AUTH_WHITELIST` 배열에 `"/game/**"` 추가

### 3. 옵션 B 선택 시
- **[MODIFY]** `ApiAuthController.java`: `accessToken`을 쿠키에 굽는 로직 추가
- **[MODIFY]** `JwtUtils.java` & `JwtAuthenticationFilter.java`: 요청 헤더뿐만 아니라 쿠키에서도 JWT를 읽어오는 로직 추가

---

## Open Questions

1. **옵션 A**와 **옵션 B** 중 어떤 구조로 프로젝트를 이끌어가고 싶으신가요? (현재 `headers.html`에 Vue.js 코드가 있는 것을 보아 **옵션 A**가 작업하시기에 조금 더 친숙하실 수 있습니다.)
2. `/game` 화면에 들어갔을 때 임시로 띄워둘 문구나 레이아웃이 있다면 말씀해 주세요.
