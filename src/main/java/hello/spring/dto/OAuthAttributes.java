package hello.spring.dto;

import hello.spring.domain.Role;
import hello.spring.domain.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String picture) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    /*'OauthAttributes' Class의 static Method임을 의미하며
    Static Method는 별도 객체의 호출 없이 사용 가능하도록 하기 위한 목적을 지니고 있다.
    of란 Custom method를 정의하며, Arguments로 3개를 받는다.
    Return value로는 ofGoogle Method를 호출한 값을 받게 된다.*/

    public static OAuthAttributes of(String registrationId,
                                     String userNameAttributeName,
                                     Map<String, Object> attributes) {
        return ofGoogle(userNameAttributeName, attributes);
    }

    // 이 또한 OAuthAttributes 클래스의 정적 메서드를 선언하는 부분이다.

    public static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    // toEntity 메서드를 통해 Service -> Database(Entity)로 Data를 전달할 때 Dto를 통해서 전달한다.
    public User toEntity() {
        return User.builder()
                .name(name)
                .email(email)
                .picture(picture)
                .role(Role.GUEST)
                .build();
    }
}
