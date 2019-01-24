package com.community.web.oauth2;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

public enum CustomOAuth2Provider {

    KAKAO {
        @Override
        public ClientRegistration.Builder getBuilder(String registrationId) {
            ClientRegistration.Builder builder = getBuilder(registrationId,
                    ClientAuthenticationMethod.POST, DEFAULT_LOGIN_REDIRECT_URL);
            builder.scope("profile");
            builder.authorizationUri("https://kauth.kakao.com/oauth/authorize");
            builder.tokenUri("https://kauth.kakao.com/oauth/token");
            builder.userInfoUri("https://kapi.kakao.com/v1/user/me");
            builder.userNameAttributeName("id");
            builder.clientName("Kakao");
            return builder;
        }
    };

    private static final String DEFAULT_LOGIN_REDIRECT_URL = "{baseUrl}/login/oauth2/code/{registrationId}";

    /**
     * CustomOAuth2Provider enum에서 사용할 수 있는 메서드
     * @param registrationId
     * @param method
     * @param redirectUri
     * @return
     */
    protected final ClientRegistration.Builder getBuilder(String registrationId,
                                                          ClientAuthenticationMethod method, String redirectUri) {
        ClientRegistration.Builder builder = ClientRegistration.withRegistrationId(registrationId);
        builder.clientAuthenticationMethod(method);
        builder.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE);
        builder.redirectUriTemplate(redirectUri);
        return builder;
    }

    /**
     * CustomOAuth2Provider enum 객체를 생성하기 위해서는 아래 abstract method를 구현해야함
     * 위 KAKAO enum 객체 또한 getBuilder를 구현하여 생성함
     * @param registrationId
     * @return
     */
    public abstract ClientRegistration.Builder getBuilder(String registrationId);
}