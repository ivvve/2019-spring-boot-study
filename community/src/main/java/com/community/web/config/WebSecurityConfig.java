package com.community.web.config;

import com.community.web.domain.enums.SocialType;
import com.community.web.oauth2.CustomOAuth2Provider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2ClientProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * 각 소셜 미디어 리소스 정보를 빈으로 등록
 * @EnableWebSecurity : 웹 시큐리티 기능 사용하겠다는 어노테이션, 그냥 붙이기만할 경우 자동설정 따라감
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 시큐리티 커스터마이징을 위해 override한 메서드
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CharacterEncodingFilter filter = new CharacterEncodingFilter();

        http.
                authorizeRequests()
                    .antMatchers("/", "/oauth2/**", "/login/**", "/css/**", "/images/**", "/js/**", "/console/**")
                        .permitAll() // static resources, index, login 페이지는 접근 허용
                    .antMatchers("/facebook") // 테스트용
                        .hasAuthority(SocialType.FACEBOOK.getRoleType()) // 해당 권한이 있어야 접근 가능
                    .antMatchers("/google") // 테스트용
                        .hasAuthority(SocialType.GOOGLE.getRoleType())
                    .antMatchers("/kakao") // 테스트용
                        .hasAuthority(SocialType.KAKAO.getRoleType())
                    .anyRequest()
                        .authenticated() // 그 외 요청은 인증된 사용자만 접근 가능
                .and()
                    .oauth2Login() // 구글, 페이스북, 깃허브 등 기본으로 제공되는 OAuth2 인증 방식이 적용됨
                        .defaultSuccessUrl("/loginSuccess")
                        .failureUrl("/loginFailure")
                .and()
                    .headers() // 응답 header 설정
                        .frameOptions().disable() // XFrameOptionsHeaderWriter의 최적화 설정을 허용하지 않음
                .and()
                    .exceptionHandling()
                        .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"))
                // 인증되지 않은 사용자가 인증이 필요한 경로 접근 시 해당 URI로 이동시킴
                .and()
                    .formLogin() // 로그인 설정
                        .successForwardUrl("/board/list") // 성공 시 포워딩 될 URI
                .and()
                    .logout() // 로그아웃에 대한 설정
                        .logoutUrl("/logout") // 로그아웃이 수행될 URL
                        .logoutSuccessUrl("/") // 성공 시 포워딩될 URI
                        .deleteCookies("JESSIONID") // JSESSIONID 삭제
                        .invalidateHttpSession(true) // 세션 invalidate
                .and()
                    .addFilterBefore(filter, CsrfFilter.class) // 문자 인코딩 필터 전에 CsrfFilter 적용
                    .csrf()
                        .disable();
    }

    /**
     * 시큐리티 starter에서 자동 설정되지만 카카오도 설정하기 위해 추가
     * @param oAuth2ClientProperties
     * @param kakaoClientId
     * @return
     */
    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(
            OAuth2ClientProperties oAuth2ClientProperties,
            @Value("${custom.oauth2.kakao.client-id}") String kakaoClientId) { // application.yml 에 설정한 kakao client-id를 불러옴

        // 기본 제공 OAuth2 인증 정보 빌드한 결과를 List로 collect
        List<ClientRegistration> registrationList = oAuth2ClientProperties.getRegistration().keySet().stream()
                .map(client -> getRegistration(oAuth2ClientProperties, client))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        // 커스텀 OAuth2 인증 정보 빌드한 결과를 위 list에 추가
        registrationList.add(CustomOAuth2Provider.KAKAO.getBuilder("kakao")
                .clientId(kakaoClientId) // 실제 사용은 client-id만
                .clientSecret("test") // clientSecret, jwkSetUri는 필요없지만 null일 경우 실행이 안되 추가함
                .jwkSetUri("test")
                .build());

        return new InMemoryClientRegistrationRepository(registrationList);
    }

    /**
     * Google, Facebook 인증을 위해 빌드한 ClientRegistration 리턴
     * @param oAuth2ClientProperties
     * @param client
     * @return
     */
    private ClientRegistration getRegistration(OAuth2ClientProperties oAuth2ClientProperties, String client) {
        if ("google".equals(client)) {
            OAuth2ClientProperties.Registration registration =
                    oAuth2ClientProperties.getRegistration().get("google");

            return CommonOAuth2Provider.GOOGLE.getBuilder(client)
                    .clientId(registration.getClientId())
                    .clientSecret(registration.getClientSecret())
                    .scope("email", "profile")
                    .build();
        }

        if ("facebook".equals(client)) {
            OAuth2ClientProperties.Registration registration =
                    oAuth2ClientProperties.getRegistration().get("facebook");

            return CommonOAuth2Provider.FACEBOOK.getBuilder(client)
                    .clientId(registration.getClientId())
                    .clientSecret(registration.getClientSecret())
                    // 페북의 graph API는 scope로는 필요한 필드를 반환해주지 않아 idm name, email, link를 파라미터로 넣어 요청하도록 설정
                    .userInfoUri("https://graph.facebook.com/me?fields=id,name,email,link")
                    .scope("email")
                    .build();
        }

        return null;
    }
}
