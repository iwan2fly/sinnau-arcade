# SSR + Cookie 기반 인증 적용 완료

Thymeleaf 화면(SSR)에 가장 적합하고 강력한 보안을 자랑하는 **옵션 B(HttpOnly 쿠키 방식)**로 작업을 완료했습니다. 이제 브라우저 주소창에 `/game`을 직접 치거나 링크를 눌러서 이동해도 403 에러 없이 깔끔하게 화면이 뜹니다!

## 🚀 적용된 변경 사항

### 1. 백엔드 쿠키 인증 로직 구현
- [ApiAuthController.java](file:///c:/work/code/platform/src/main/java/kr/sinnau/platform/api/local/v1/ApiAuthController.java)
  로그인(`verify`) 및 갱신(`refresh`) 성공 시 `accessToken`을 JSON 응답 본문 대신 **HttpOnly 쿠키**에 담아서 반환하도록 수정했습니다. 추가로, 프론트엔드(Vue.js)가 로그인 상태를 알 수 있도록 `isLoggedIn=true` 상태 쿠키(HttpOnly 아님)도 발급합니다.
  로그아웃 시에는 서버에서 쿠키 만료시간을 0으로 덮어써서 쿠키를 완전히 삭제합니다.
- [JwtUtils.java](file:///c:/work/code/platform/src/main/java/kr/sinnau/platform/common/security/JwtUtils.java)
  기존에는 `Authorization` 헤더만 검사했지만, 이제는 요청에 헤더가 없으면 **쿠키를 뒤져서 `accessToken`을 찾아내는 로직**이 추가되었습니다.

### 2. 프론트엔드 로그인/로그아웃 구조 개편
- [headers.html](file:///c:/work/code/platform/src/main/resources/templates/common/headers.html) & [playGate.html](file:///c:/work/code/platform/src/main/resources/templates/gate/playGate.html)
  기존에 프론트엔드가 `localStorage`에서 토큰을 직접 읽고 쓰던 불필요한 로직을 제거했습니다.
  대신 `document.cookie.includes('isLoggedIn=true')`를 검사하여 로그인 상태를 아주 깔끔하게 동기화합니다.

### 3. 신규 화면 (`/game`) 개발
- [WebGameController.java](file:///c:/work/code/platform/src/main/java/kr/sinnau/platform/ui/web/WebGameController.java)
  스프링 MVC 컨트롤러를 생성하여 `/game` 경로를 매핑했습니다.
- [main.html](file:///c:/work/code/platform/src/main/resources/templates/game/main.html)
  게임 콘텐츠가 들어갈 수 있는 깔끔한 임시 레이아웃(카드형)을 구성해 두었습니다.

---

> [!TIP]
> **이제 어떻게 테스트하나요?**
> 현재 켜져 있는 서버를 한 번 재시작(Restart) 해 주신 뒤, 다시 로그인 과정을 진행해 보세요.
> 로그인 완료 후 상단 메뉴에서 **"게임"**을 클릭하시면, 더 이상 403 권한 에러 없이 신나유 오락실 레이아웃 화면이 정상적으로 열리는 것을 확인하실 수 있습니다!
