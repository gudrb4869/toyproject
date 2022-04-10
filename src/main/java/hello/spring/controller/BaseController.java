package hello.spring.controller;

import hello.spring.config.auth.SessionUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Controller
public class BaseController {
    private final HttpSession httpSession;

    @GetMapping("/")
    public String index(Model model) {
        SessionUser user = (SessionUser) httpSession.getAttribute("user");

        if (user != null) {
            /*
            (SessionUser) httpSession.getAttribute("user")
            => 앞서 작성된 CustomOAuth2UserService에서 로그인 성공 시 세션에 SessionUser를 저장하도록 구성
            => 즉, 로그인 성공시 httpSerssion.getAttribute("user")에서 값을 가져올 수 있다.
            user가 null 값일 경우 index 페이지에 model 값에 attribute를 전달하기 못할 것이다.
            */
            model.addAttribute("userName", user.getName());
            model.addAttribute("userImg", user.getPicture());
        }

        return "index";
    }
}
