# 스프링 데이터 JPA: Auditing
> 엔티티의 변경 시점에 언제, 누가 변경했는지에 대한 정보를 기록하는 기능  
## 스프링 데이터 JPA의 Auditing 실습
#### Account 클래스 생성
> Auditing 기록을 남기기 위한 사용자 정보 클래스  
```java
@Entity
public class Account {

    @Id @GeneratedValue
    private Long id;
    private String username;
    private String password;
}
```

#### Comment 클래스에 Auditing 정보를 남기기위한 항목 추가
```java
@CreatedDate
private Date created;

@LastModifiedDate
private Date updated;

@CreatedBy
@ManyToOne
private Account createdBy;

@LastModifiedBy
@ManyToOne
private Account updatedBy;
```

#### Auditing 를 사용할 클래스 상단에 @EntityListeners 설정
```java
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Comment {
```

#### AccountAuditAware 클래스 생성
> 유저정보를 꺼내올 수 있는 기능  
```java
@Service
public class AccountAuditAware implements AuditorAware<Account> {
    @Override
    public Optional<Account> getCurrentAuditor() {
        System.out.println("looking for current user");
        return Optional.empty();
    }
}
```

#### @EnableJpaAuditing
- Auditing는 자동설정 되지 않으므로 아래와 같이 수동으로 설정
- AccountAuditAware 도 빈 이름으로 설정(클래스이름은 안됨)
```java
@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "accountAuditAware")
```

#### 테스트 로직
> accountAuditAware 빈도 사용해야 하므로 @SpringBootTest 로 테스트  
```java
@RunWith(SpringRunner.class)
@SpringBootTest
public class CommentRepositoryTest {

    @Autowired
    CommentRepository comments;

    @Autowired
    PostRepository posts;

    @Test
    public void getComment() {
        Post post = new Post();
        post.setTitle("jpa");
        Post savedPost = posts.save(post);

        Comment comment = new Comment();
        comment.setComment("spring data jpa projection");
        comment.setPost(savedPost);
        comment.setUp(10);
        comment.setDown(1);
        comments.save(comment);

        comments.findByPost_Id(savedPost.getId(), CommentOnly.class).forEach(c -> {
            System.out.println("========================");
//            System.out.println(c.getVotes());
            System.out.println(c.getComment());
        });
    }
}
```

 
## 아쉽지만 이 기능은 스프링 부트가 자동 설정 해주지 않습니다
1. 메인 애플리케이션 위에 @EnableJpaAuditing 추가
2. 엔티티 클래스 위에 @EntityListeners(AuditingEntityListener.class) 추가
3. AuditorAware 구현체 만들기
4. @EnableJpaAuditing에 AuditorAware 빈 이름 설정하기

### 스프링 시큐리티에서 유저정보를 가져와서 넣을 수 있는 방법
https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#auditing
```java
class SpringSecurityAuditorAware implements AuditorAware<User> {

  public Optional<User> getCurrentAuditor() {

    return Optional.ofNullable(SecurityContextHolder.getContext())
			  .map(SecurityContext::getAuthentication)
			  .filter(Authentication::isAuthenticated)
			  .map(Authentication::getPrincipal)
			  .map(User.class::cast);
  }
}
```
 
## JPA의 라이프 사이클 이벤트
http://docs.jboss.org/hibernate/orm/5.4/userguide/html_single/Hibernate_User_Guide.html#events-jpa-callbacks
https://docs.jboss.org/hibernate/orm/4.0/hem/en-US/html/listeners.html
> Hibernate가 제공하는 기능 어떠한 Entity에 변화가 일어났을때 특정한 콜백을 실행할 수 있는 이벤트를 발생시켜 줌  
> 콜백을 Entity에 정의할 수 있음  
> 이 이벤트를 이용해서 Audit 기능을 구현할 수도 있음  
- @PrePersist: Entity가 저장이 되기 전에 호출
- @PreUpdate
- @PreRemove
- ...

## 예제 
```java
@Entity
public class Comment {
    @PrePersist
    public void prePersist() {
        System.out.println("Pre Persist is called");
    }
}
```