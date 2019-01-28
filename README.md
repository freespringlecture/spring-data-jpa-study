# 스프링 데이터 Common: 도메인 이벤트
> 도메인(Entity 클래스) 관련 변화를 이벤트를 발생시키기  
> 도메인을 하는 이벤트 리스너가 도메인 Entity 클래스의 변화를 감지하고 이벤트 기반의 프로그래밍을 할 수 있음  
 
## 스프링 프레임워크의 이벤트 관련 기능
https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#context-functionality-events
  
> 스프링을 사용하는 모든 프로젝트에는 이벤트를 만들어서 Publishing 하고 Listening 하는 기능이 내재되어있음  
> ApplicationContext가 이벤트 Publisher 임  
> BeanFactory, EventPublisher 인터페이스를 상속 받았음  
```java
ApplicationContext extends ApplicationEventPublisher
```
- 이벤트: extends ApplicationEvent
- 리스너
  - implements ApplicationListener<E extends ApplicationEvent>
  - @EventListener
 
## 스프링 데이터의 도메인 이벤트 Publisher
https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#core.domain-events
  
> 스프링 데이터가 자동 Publishing 기능을 제공 쌓여있던 이벤트들을 save하는 순간 다 보내줌  
- @DomainEvents: 이벤트를 모아놓는 곳
- @AfterDomainEventPublication: 이벤트를 자동으로 비워주는 메서드

## 도메인 이벤트 실습
#### PostPublishedEvent 클래스 생성
> 이벤트를 발생시키는 곳이 post 이고 이벤트를 받는 리스너 쪽에서 어떤 post에 대한 이벤트였는지  
> post를 참조할 수 있도록 getter 추가  
```java
public class PostPublishedEvent extends ApplicationEvent {

    private final Post post;
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public PostPublishedEvent(Object source) {
        super(source);
        this.post = (Post) source;
    }

    public Post getPost() {
        return post;
    }
}
```

#### PostListner 클래스 생성
> Post Event Listner 구현  
```java
public class PostListner implements ApplicationListener<PostPublishedEvent> {
    @Override
    public void onApplicationEvent(PostPublishedEvent event) {
        System.out.println("-----------------");
        System.out.println(event.getPost() + " is published!");
        System.out.println("-----------------");
    }
}
```

#### 테스트 Config 구현
> PostListner를 빈으로 등록해줌  
```java
@Configuration
public class PostRepositoryTestConfig {

    @Bean
    public PostListener postListner() {
        return new PostListener();
    }
}
```

#### 테스트 로직 구현
- `@Import(PostRepositoryTestConfig.class)` 로 PostListener 클래스를 빈 으로 등록  
- 테스트가 실행되면 테스트에서는 event를 Publish 했을 때 PostListener이 잡아서 메세지를 출력  
- 슬라이싱테스트라서 DataJpa 관련된 빈들 만 등록되므로 `@Import`를 사용해 별도로 `PostListener`을 빈으로 등록
```java
@RunWith(SpringRunner.class)
@DataJpaTest
@Import(PostRepositoryTestConfig.class)
public class PostRepositoryTest {

    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void event() {
        Post post = new Post();
        post.setTitle("event");
        PostPublishedEvent event = new PostPublishedEvent(post);

        applicationContext.publishEvent(event);
    }
}
```


## AbstractAggregationRoot
> 스프링 데이터에 이미 위의 어노테이션이 적용되어 이벤트 모아두는 곳, 이벤트 비우는 곳이 구현되어 있음  
> 이벤트를 직접 만들고 이벤트를 Publishing 할 필요가 없음  
- extends AbstractAggregationRoot<E>
- 현재는 save() 할 때만 발생
  > save 할때 이벤트를 만들어 넣으면 됨  
  > save 할때 자동으로 AbstractAggregationRoot를 통해 domainEvents 에 모아져있던 이벤트를 다 발생시킴  
  > 그러면 만들어 두었던 EventListener가 동작함  
  > 직접 이벤트를 던져주는 코드가 사라짐  

## AbstractAggregationRoot를 통해 이벤트 등록

#### 아래와 같이 Post 클래스 AbstractAggregateRoot를 상속받아서 구현
> 상속받아서 이벤트를 자동으로 등록해주는 로직을 구현해주면 별도의 이벤트 등록 코드를 구현하지 않아도 이벤트가 자동으로 등록됨  
```java
@Entity
public class Post extends AbstractAggregateRoot<Post> {

    @Id @GeneratedValue
    private Long id;

    private String title;

    @Lob
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    public Post publish() {
        this.registerEvent(new PostPublishedEvent(this));
        return this;
    }
}
```

#### 테스트 로직
> 아래와 같이 save 할때 `post.publish()`를 호출하면 이벤트가 호출되어 이벤트 리스너가 동작함  
```java
@Test
    public void crud() {
        Post post = new Post();
        post.setTitle("hibernate");

        // save 하기 전에 postRepository.contains안에 post가 들어있는지 검증
        // post가 들어있지 않은 Transient 상태
        assertThat(postRepository.contains(post)).isFalse();

        postRepository.save(post.publish());

        // post가 들어있는 Persist 상태
        assertThat(postRepository.contains(post)).isTrue();

        postRepository.delete(post);
        postRepository.flush();
    }
```

## PostListener에서 @EventListener 사용
> 둘다 스프링 프레임워크가 제공해주는 기능임  
### 기존 로직
```java
public class PostListener implements ApplicationListener<PostPublishedEvent> {
    @Override
    public void onApplicationEvent(PostPublishedEvent event) {
        System.out.println("-----------------");
        System.out.println(event.getPost() + " is published!");
        System.out.println("-----------------");
    }
}
```

### @EventListener 적용 로직
> 반드시 PostListener이 빈으로 등록되어 있어야함  
```java
public class PostListener {
    @EventListener
    public void onApplicationEvent(PostPublishedEvent event) {
        System.out.println("-----------------");
        System.out.println(event.getPost() + " is published!");
        System.out.println("-----------------");
    }
}
```

## PostListener 클래스 안만들고 TestConfig 파일에 직접등록
> 아래와 같이 PostListener 클래스를 만들지 않고 직접 TestConfig에 등록해도됨  
```java
@Configuration
public class PostRepositoryTestConfig {

    @Bean
    public ApplicationListener<PostPublishedEvent> postListener() {
        return new ApplicationListener<PostPublishedEvent>() {
            @Override
            public void onApplicationEvent(PostPublishedEvent event) {
                System.out.println("-----------------");
                System.out.println(event.getPost() + " is published!");
                System.out.println("-----------------");
            }
        };
    }
}
```