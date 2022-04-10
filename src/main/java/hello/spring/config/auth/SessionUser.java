package hello.spring.config.auth;

import hello.spring.domain.User;
import lombok.Getter;

import java.io.Serializable;
/*
User 클래스가 이미 있는데 왜 따로 SessionUser 클래스를 생성하였을까?
- Entity 클래스는 직렬화 코드를 넣지 않는게 좋다
- Entity 클래스에는 언제 다른 Entity와 Relationship이 형성될지 모른다.
- @OneToMany, @ManyToMany등 자식 Entity를 갖고 있다면 직렬화 대상에 자식들까지 포함되니 성능 이슈, 부수 효과가 발생할 확률이 높다
=> 그래서 직렬화 기능을 가진 세션 DTO를 하나 추가로 만든 것이 더 좋은 방법이다.
*/
@Getter
public class SessionUser implements Serializable {
    private String name;
    private String email;
    private String picture;

    public SessionUser(User user) {
        this.name = user.getName();
        this.email = user.getEmail();
        this.picture = user.getPicture();
    }
}
