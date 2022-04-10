package hello.spring.service;

import hello.spring.config.auth.SessionUser;
import hello.spring.domain.User;
import hello.spring.dto.OAuthAttributes;
import hello.spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.Collections;

// Spring Security 로 OAuth2 Google Login 관련 Service
@RequiredArgsConstructor
@Transactional
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        /*
        현재 로그인 진행 중인 서비스를 구분하는 코드이다.
        본 글은 Google로만 Login을 진행하기 때문에 필요가 없을 수 있지만
        추후 Naver, Kakao 등 소셜 로그인이 추가될 때 어떤 소셜 제공자인지 구분하기 위한 부분이다.
        */
        // OAuth2 서비스 id (구글, 카카오, 네이버)
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // - OAuth2 로그인 진행 시 키가 되는 필드값. Primary Key와 같은 의미
        // OAuth2 로그인 진행 시 키가 되는 필드 값(PK)
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        /*
        - OAuth2UserService를 통해 가져온 OAuth2User의 attribute를 attributes란 DTO로 담는다.
        - 이후, saveOrUpdate 메서드의 인자로 attributes를 넣어준다.
        - 이후, 세션에 사용자 정보를 저장하기 위해 SessionUser 클래스에 인자로 user를 넣어서 httpSession의 Attribute를 추가해준다.
        => 세션에 저장하기 위해 기 생성한 User Class를 세션에 저장하려고 하면
        User 클래스에 직렬화를 구현하지 않았다는 에러가 발생하여 따로 SessionUser를 구현
        */
        // OAuth2UserService
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());
        User user = saveOrUpdate(attributes);
        httpSession.setAttribute("user", new SessionUser(user)); // SessionUser (직렬화된 dto 클래스 사용)

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    // 말 그대로 유저를 생성하거나 수정하는 서비스 로직이다.
    // 유저 생성 및 수정 서비스 로직
    private User saveOrUpdate(OAuthAttributes attributes){
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                .orElse(attributes.toEntity());
        return userRepository.save(user);
    }
}
