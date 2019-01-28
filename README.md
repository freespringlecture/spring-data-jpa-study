# 스프링 데이터 Common: Web 2부: DomainClassConverter

## 실습 코드
> Integration 테스트 Application의 모든 빈을 등록해서 하는 테스트  

#### Post 클래스 생성
```java
@Entity
public class Post {

    @Id @GeneratedValue
    private String id;
    private String title;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
```

#### PostRepository 인터페이스 생성
```java
public interface PostRepository extends JpaRepository<Post, Long> {
}
```

#### PostController 클래스 생성 
```java
@RestController
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @GetMapping("/posts/{id}")
    public String getPost(@PathVariable Long id) {
        Optional<Post> byId = postRepository.findById(id);
        Post post = byId.get();
        return post.getTitle();
    }
}
```

#### 테스트 코드 구현
```java
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
//@ActiveProfiles("test")
public class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PostRepository postRepository;

    @Test
    public void getPost() throws Exception {
        Post post = new Post();
        post.setTitle("jpa");
        postRepository.save(post);

        mockMvc.perform(get("/posts/1"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("jpa"));
    }

}
```

## 스프링 Converter
https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/core/convert/converter/Converter.html
> 하나의 타입을 다른 타입으로 변환하는 인터페이스  
> 스프링MVC 에서 둘다 사용할 수 있음  
- Converter: 바인딩을 받아서 Long으로 변환이 되고 Long이 id 타입이니까 그다음에 변환이 일어남
- Formatter: 무조건 문자열 기반으로 어떠한 문자열이 들어왔을 때 다른 타입으로 어떻게 변환할 것인가  
  또는 Object로 받았을 때 그것을 어떻게 문자열로 변환을 할 것인가  
  String이 아닌경우 Formatter에 쓰일 수 없음  
https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/format/Formatter.html

## DomainClassConverter
> 자동으로 ConverterRegistry에 들어감  
> ConverterRegistry에 들어가면 SpringMVC에서 어떤 데이터를 바인딩 받아서 Converting 할 때 참고해서 사용  
> 총 두가지 Converter가 등록됨  
- ToEntityConverter: 어떠한 Entity의 Id를 받아서 그 Entity 타입으로 변환하는 Converter
- ToIdConverter: Entity를 그 Entity의 Id 타입으로 변환하는 Converter
- 아래의 실습 처럼 id 타입이 String이 아닌경우 Formatter에 쓰일 수 없음


## DomainClassConverter 실습
> ToEntityConverter는 Repository를 사용해서 Id를 가지고 findById를 하므로 정확하게 아래의 코드와  
> 일치하는 경우가 자동으로 벌어짐  
```java
Optional<Post> byId = postRepository.findById(id);
Post post = byId.get();
```

#### 기존 코드
```java
@GetMapping("/posts/{id}")
public Post getPost(@PathVariable Long id) {
    Optional<Post> byId = postRepository.findById(id);
    Post post = byId.get();
    return post;
}
```

#### 변경 코드
> 위의 코드 만큼 생략하고 바로 Post로 받아올 수 있음  
> 더이상 post 매개변수 이름이 @PathVariable 이름과 같지 않기 때문에 바인딩 받을 때 사용할 @PathVariable 이름을  
> 반드시 @PathVariable("id") 라고 명시해 줘야함  
> id에 해당하는것을 post로 받아오겠다라고 하면 그때 DomainClassConverter가 동작함  
```java
@GetMapping("/posts/{id}")
public String getPost(@PathVariable("id") Post post) {
    return post.getTitle();
}
```