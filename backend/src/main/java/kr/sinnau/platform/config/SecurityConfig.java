package kr.sinnau.platform.config;

import kr.sinnau.platform.common.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    /**
     * 1	DisableEncodeUrlFilter	URL에 세션 ID가 포함되는 것을 막음
     * 2	WebAsyncManagerIntegrationFilter	비동기 요청 시에도 보안 컨텍스트를 유지하게 함
     * 3	SecurityContextHolderFilter	이전 요청의 SecurityContext를 로드 (Stateless에선 매번 비어있음)
     * 4	HeaderWriterFilter	응답에 보안 관련 헤더(X-Frame-Options 등)를 추가
     * 5	CorsFilter	CORS 설정(허용된 도메인 등)을 체크 (설정 시 작동)
     * 6	LogoutFilter	로그아웃 URL 요청을 가로채서 처리
     * 7	------>>> JwtAuthenticationFilter	[우리 필터] 토큰을 검사해서 인증(SecurityContext) 처리
     * 8	UsernamePasswordAuthenticationFilter	아이디/비번 기반 폼 로그인 처리 (우리는 여기서 이미 인증 완료됨)
     * 9	DefaultLoginPageGeneratingFilter	기본 로그인 페이지 생성 (우리는 사용 안 함)
     * 10	BasicAuthenticationFilter	HTTP Basic 인증 헤더 처리
     * 11	RequestCacheAwareFilter	인증 후 이전 요청 페이지로 리다이렉트하기 위해 요청 저장
     * 12	SecurityContextHolderAwareRequestFilter	서블릿 API(HttpServletRequest)에 시큐리티 관련 메서드 추가
     * 13	AnonymousAuthenticationFilter	아직 인증 안 된 유저를 'anonymousUser' 권한으로 설정
     * 14	SessionManagementFilter	세션 정책(Stateless 등) 관리
     * 15	ExceptionTranslationFilter	[중요] 뒤에서 발생한 인증/인가 예외를 잡아 응답으로 변환
     * 16	AuthorizationFilter	[최종 관문] anyRequest().authenticated() 등의 권한을 최종 체크
     * 17	DispatcherServlet	필터를 모두 통과하면 드디어 Controller에 도달!
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;



    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                // 1. JWT를 쓰니까 세션은 생성하지 않도록 설정
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(SecurityConstants.AUTH_WHITELIST).permitAll()
                        .anyRequest().authenticated()
                )
                // 4. UsernamePasswordAuthenticationFilter 실행 전에 우리 JWT 필터 실행
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

/**
 * 직접 필터 클래스를 새로 만드는 일보다는, "기존 필터의 설정을 바꾸거나", "특정 역할을 하는 필터(Handler)를 갈아 끼우는" 작업이 주를 이룹니다.
 * 앞으로 신나유 프로젝트를 진행하면서 손보게 될 확률이 높은 필터나 설정들은 다음과 같습니다.
 *
 * 1. ExceptionTranslationFilter (15번) 커스텀
 * 가장 많이 손보게 될 부분입니다. 토큰이 없거나 권한이 없을 때, 스프링이 기본으로 뱉는 에러는 우리 프로젝트의 ApiResponse 규격과 다릅니다.
 * 하는 일: "인증 실패 시 내가 만든 AuthenticationEntryPoint를 실행해!"라고 설정해서 에러 응답을 예쁘게 통일시킵니다.
 *
 * 2. CorsFilter (5번) 설정
 * 프론트엔드(React 등)와 연결할 때 필수입니다.
 * 하는 일: "어느 도메인에서 오는 요청을 허용할 것인가?"를 결정합니다. 직접 필터를 만들기보다는 SecurityConfig에서 CorsConfigurationSource를 설정하는 식으로 손봅니다.
 *
 * 3. LogoutFilter (6번)
 * 만약 JWT를 DB나 Redis에 저장해서 로그아웃 처리를 엄격하게 하고 싶다면 이 부분을 건드립니다.
 * 하는 일: 로그아웃 요청이 왔을 때 토큰을 만료(Blacklist) 처리하는 로직을 끼워 넣습니다.
 *
 * 4. AuthorizationFilter (16번)
 * 직접 필터를 수정하기보다는 SecurityConfig의 authorizeHttpRequests 설정을 통해 제어합니다.
 * 하는 일: "관리자(ADMIN)만 들어올 수 있는 API", "일반 유저(USER)만 가능한 API" 등을 세밀하게 나눌 때 손보게 됩니다.
 *
 * 💡 요약하자면
 * 우리가 필터 자체를 새로 짜는 것은 오늘 만든 JwtAuthenticationFilter가 거의 유일할 거예요.
 * 나머지는 대부분 **"스프링이 이미 만들어둔 필터의 설정값을 우리 입맛에 맞게 바꾸는 작업"**이 될 겁니다. 즉, 부품을 새로 깎는 게 아니라 이미 있는 기계의 스위치를 조절하거나 옵션을 추가하는 식이죠.
 * 이제 필터 체인의 전체적인 그림이 완벽하게 그려지셨을 것 같습니다. 오늘 공부하시느라 고생 정말 많으셨어요! 푹 쉬시고 기분 좋게 다음 단계에서 뵙겠습니다. 😊
 */