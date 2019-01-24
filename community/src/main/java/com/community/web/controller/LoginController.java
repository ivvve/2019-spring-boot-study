package com.community.web.controller;

import com.community.web.annotation.SocialUser;
import com.community.web.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    /**
     * 아래 복잡한 로직은 AOP를 통해 깔끔하게 변경 가능하다
     *
     * @see SocialUser
     * @see com.community.web.resolver.UserArgumentResolver
     * @param user
     * @return
     */
    @GetMapping("/loginSuccess")
    public String loginComplete(@SocialUser User user) {
        return "redirect:/board/list";
    }

    /**
     * User 정보를 가져와 세션에 담는 과정
     * 하지만 너무 복잡하고 번거롭다.
     * @param session
     * @return
     */
    //    @GetMapping("/{facebook|google|kakao}/complete")
    //    public String loginComplete(HttpSession session) {
    //        OAuth2Authentication auth2Authentication =
    //                (OAuth2Authentication) SecurityContextHolder.getContext().getAuthentication();
    //
    //        // 인증 받은 User 정보를 map에 담아 가져옴
    //        Map<String, String> map = (HashMap<String, String>) auth2Authentication.getUserAuthentication().getDetails();
    //
    //        // 위에서 받은 map에서 user 정보를 가져온다. : <-- too complex and not type safe
    //        session.setAttribute("user", User.builder()
    //                .name(map.get("name"))
    //                .email(map.get("email"))
    //                .principal(map.get("id"))
    //                .socialType(SocialType.KAKAO)
    //                .createdDate(LocalDateTime.now())
    //                .build()
    //        );
    //
    //        return "redirect:/board/list";
    //    }
}