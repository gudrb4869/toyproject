package hello.spring.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import javax.persistence.*;

// Board : 실제 DB와 매칭될 클래스 (Entity Class)

// JPA에서는 프록시 생성을 위해 기본 생성자를 반드시 하나 생성해야 한다.
// 생성자 자동 생성 : NoArgsConstructor, AllArgsConstructor
// NoArgsConstructor : 객체 생성 시 초기 인자 없이 객체를 생성할 수 있다.
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 외부에서의 생성을 열어 둘 필요가 없을 때 / 보안적으로 권장된다.
@Getter
@Entity
@Table(name = "board") // 이거 보고 테이블 생성
public class Board extends Time {

    @Id // PK Field
    @GeneratedValue(strategy = GenerationType.IDENTITY) // PK의 생성 규칙
    private Long id;

    @Column(length = 10, nullable = false)
    private String writer;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @OneToOne(fetch = FetchType.LAZY)
    private User user;

    // Java 디자인 패턴, 생성 시점에 값을 채워줌
    @Builder
    public Board(Long id, String writer, String title, String content, User user) {
        // Assert 구문으로 안전한 객체 생성 패턴을 구현
        Assert.hasText(writer, "writer must not be empty");
        Assert.hasText(title, "title must not be empty");
        Assert.hasText(content, "content must not be empty");
        this.id = id;
        this.writer = writer;
        this.title = title;
        this.content = content;
        this.user = user;
    }
}
