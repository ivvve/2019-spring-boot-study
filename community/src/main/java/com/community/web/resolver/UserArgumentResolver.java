package com.community.web.resolver;

import com.community.web.annotation.SocialUser;
import com.community.web.domain.User;
import com.community.web.domain.enums.SocialType;
import com.community.web.repository.UserRepository;
import org.springframework.core.MethodParameter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static com.community.web.domain.enums.SocialType.*;

/**
 * 컨트롤러에 들어오는 파라미터를 커스텀하게 가공해서 사용하고 싶을 때 사용
 */
@Component
public class UserArgumentResolver implements HandlerMethodArgumentResolver {
    private UserRepository userRepository;

    public UserArgumentResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * @SocialUser가 달려있는 User 파라미터에 해당 Resolver를 적용시킴
     * @param parameter
     * @return
     */
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return (parameter.getParameterAnnotation(SocialUser.class) != null) &&
                parameter.getParameterType().equals(User.class);
    }

    /**
     * HttpSession에서 "user"로 저장된 User를 가져옮
     * @param parameter
     * @param mavContainer
     * @param webRequest
     * @param binderFactory
     * @return
     * @throws Exception
     */
    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpSession session =
                ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession();

        User user = (User) session.getAttribute("user");

        return getUser(user, session);
    }

    /**
     * 파라미터 user가 null이 아닐 경우 (session에 저장된 "user" attribute가 있을 경우) 해당 객체를 그대로 return
     * 파라미터 user가 null일 경우 (session에 저장된 "user" attribute가 없을 경우)
     *      OAuth token에서 정보를 가져온 뒤 이를 기존 저장된 user에 담거나 새로 저장한 user에 담는다.
     *      정보를 담은 후 세션에 담는다.
     * @param user
     * @param session
     * @return
     */
    private User getUser(User user, HttpSession session) {
        if (user != null) {
            return user;
        }

        // SecurityContextHolder에서 OAuth token을 가져온다.
        OAuth2AuthenticationToken authenticationToken =
                (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        // token에서 개인 정보를 map에 담아 가져온다.
        Map<String, Object> map = authenticationToken.getPrincipal().getAttributes();
        // token에서 가져온 개인 정보를 User 객체에 담는다.
        User convertUser = convertUser(authenticationToken.getAuthorizedClientRegistrationId(), map);

        //        user = userRepository.findByEmail(convertUser.getEmail()).orElse(null);
        //
        //        if (user == null) {
        //            user = userRepository.save(convertUser);
        //        }

        // 위 코드 리팩토링
        user = userRepository.findByEmail(convertUser.getEmail()).orElse(userRepository.save(convertUser));

        setRoleIfNotSame(user, authenticationToken, map);
        session.setAttribute("user", user);

        return user;
    }

    /**
     * 사용자의 인증 소셜 미디어 타입에 따라 User 객체 만든다.
     * @param authority
     * @param map
     * @return
     */
    private User convertUser(String authority, Map<String, Object> map) {
        if (FACEBOOK.isEquals(authority)) {
            return getModernUser(FACEBOOK, map);
        } else if (GOOGLE.isEquals(authority)) {
            return getModernUser(GOOGLE, map);
        } else if (KAKAO.isEquals(authority)) {
            return getKakaoUser(map);
        }

        return null;
    }

    /**
     * 페이스북, 구글과 같이 공통된 명명규칙을 가진 그룹을 User 객체로 매핑
     * @param socialType
     * @param map
     * @return
     */
    private User getModernUser(SocialType socialType, Map<String, Object> map) {
        return User.builder()
                .name(String.valueOf(map.get("name")))
                .email(String.valueOf(map.get("email")))
                .principal(String.valueOf(map.get("id")))
                .socialType(socialType)
                .createdDate(LocalDateTime.now())
                .build();
    }

    /**
     * 카카오 회원을 위한 메서드
     * token에서 가져온 정보를 User 객체로 매핑
     * @param map
     * @return
     */
    private User getKakaoUser(Map<String, Object> map) {
        Map<String, String> propertyMap = (HashMap<String, String>) ((Object) map.get("properties"));

        String email = String.valueOf(map.get("id")) + "@local.com";

        return User.builder()
                .name(propertyMap.get("nickname"))
                // 카카오 정책이 변경되서 email이 필수가 아니라 가져올 수 없다
//                .email(String.valueOf(map.get("kaccount_email")))
                .email(email)
                .principal(String.valueOf(map.get("id")))
                .socialType(KAKAO)
                .createdDate(LocalDateTime.now())
                .build();
    }

    /**
     * 인증이 권한을 가지고 있는지 체크하는 메서드
     * 만약 저장된 권한이 없으면 SecurityContextHolder를 사용하여 해당 소셜 미디어 타입으로 권한을 저장
     * @param user
     * @param authenticationToken
     * @param map
     */
    private void setRoleIfNotSame(User user, OAuth2AuthenticationToken authenticationToken, Map<String, Object> map) {
        if (!authenticationToken.getAuthorities().contains(new SimpleGrantedAuthority(user.getSocialType().getRoleType()))) {

            SecurityContextHolder.getContext().setAuthentication(
                    new UsernamePasswordAuthenticationToken(map, "N/A",
                            AuthorityUtils.createAuthorityList(user.getSocialType().getRoleType())));
        }
    }
}