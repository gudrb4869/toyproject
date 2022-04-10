package hello.spring.config;

import hello.spring.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@RequiredArgsConstructor
@EnableWebSecurity // - Spring Security 설정들을 활성화하기 위한 애노테이션이다.
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .headers().frameOptions().disable()// - h2-console 화면을 사용하기 위해 해당 옵션들을 disable
                .and()
                /*
                - URL별 권한 관리를 설정하는 옵션의 시작점
                - authorizeRequests가 선언되어야만 antMatchers 옵션을 사용이 가능하다.
                */
                .authorizeRequests()
                /*
                - 권한 관리 대상을 지정하는 옵션이다.
                - URL, HTTP 메서드별로 관리가 가능하다.
                */
                .antMatchers("/", "/Savory-gh-pages/**").permitAll()
                /*
                - 설정된 값을 이외 나머지 URL들을 나타낸다.
                - authenticated() 메서드를 통해 나머지 URL들은 모두 인증된 사용자에게만 허용하게 한다.
                */
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/")
                .and()
                .logout().logoutSuccessUrl("/")
                .and()
                /*
                - OAuth2 로그인 기능에 대한 여러 설정의 진입점
                - userInfoEndpoint : OAuth2 로그인이 성공된 이후 사용자 정보를 가져올 때 설정을 담당한다.
                - userService
                : 소셜 로그인 성공 시 후속 조치를 진행 할 UserService 인터페이스의 구현체를 등록한다.
                : 서비스 제공자(Google, Kakao, ..)에서 사용자 정보를 가져온 정보 (Email, Name, Picture, ..)을 기반으로 추가로 진행하고자 하는 기능들을 명시할 수 있다.
                (예를 들어, 해당 정보를 가지고 우리 웹 서비스의 DB에 사용자들을 저장한다든지 등등..) */
                .oauth2Login().userInfoEndpoint().userService(customOAuth2UserService);

    }
}
