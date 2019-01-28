# 스프링 데이터 Common: Web 3부: Pageable 과 Sort 매개변수
## 스프링 MVC HandlerMethodArgumentResolver
https://docs.spring.io/spring/docs/current/javadoc-api/org/springframework/web/method/support/HandlerMethodArgumentResolver.html
  
> 스프링 MVC 핸들러 메소드의 매개변수로 받을 수 있는 객체를 확장하고 싶을 때 사용하는 인터페이스  
> 추가로 스프링 데이터 JPA의 Web 지원기능을 사용하면 Pageable과 Sort도 매개변수로 사용할 수 있음  
 
### 페이징과 정렬 관련 매개변수
- page: 0부터 시작.
- size: 기본값 20.
- sort: property,property(,ASC|DESC)
- 예) sort=created,desc&sort=title (asc가 기본값)

## 실습
> 저번 프로젝트와 이어서  
#### 매개변수를 Pageable로 받아서 Page로 리턴하는 API 추가
```java
@RestController
public class PostController {

    @Autowired
    private PostRepository posts;

    @GetMapping("/posts")
    public Page<Post> getPosts(Pageable pageable) {
        return posts.findAll(pageable);
    }
}
```

#### 테스트 코드
> 파라메터로 페이정보를 넘기고 응답받는 테스트  
```java
@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PostControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PostRepository postRepository;

    @Test
    public void getPosts() throws Exception {

        Post post = new Post();
        post.setTitle("jpa");
        postRepository.save(post);

        mockMvc.perform(get("/posts")
                    .param("page", "0")
                    .param("size", "10")
                    .param("sort", "created,desc")
                    .param("sort", "title"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title", is("jpa")));
    }
}
```