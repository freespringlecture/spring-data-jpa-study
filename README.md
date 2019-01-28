# 2-7. 스프링 데이터 Common: 커스텀 리포지토리
> 쿼리 메소드(쿼리 생성과 쿼리 찾아쓰기)로 해결이 되지 않는 경우 직접 코딩으로 구현 가능  
- 스프링 데이터 리포지토리 인터페이스에 기능 추가
- 스프링 데이터 리포지토리 기본 기능 덮어쓰기 가능

## 구현 방법
1. 커스텀 리포지토리 인터페이스 정의 
2. 인터페이스 구현 클래스 만들기 (기본 접미어는 Impl)
3. 엔티티 리포지토리에 커스텀 리포지토리 인터페이스 추가
 
## 기능 추가하기 예제 실습
#### Post 클래스 생성
```java
@Entity
public class Post {
    @Id @GeneratedValue
    private Long id;

    private String title;

    @Lob
    private String content;

    @Temporal(TemporalType.TIMESTAMP)
    private Date created;
}
```

#### PostCustomRepository 인터페이스 생성
> Custom Repository 인터페이스  
```java
public interface PostCustomRepository {
    List<Post> findMyPost();
}
```

#### PostCustomRepositoryImpl 구현체 생성
> PostCustomRepository 구현체 추가  
```java
@Repository
@Transactional
public class PostCustomRepositoryImpl implements PostCustomRepository{

    @Autowired
    EntityManager entityManager;

    @Override
    public List<Post> findMyPost() {
        System.out.println("custom findMyPost");
        return entityManager.createQuery("SELECT p FROM Post AS p ", Post.class).getResultList());
    }
}
```

#### PostRepository 인터페이스 생성
> PostRepository 인터페이스에 JpaRepository와 PostCustomRepository 상속  
```java
public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository {
}
```
 
## 기본 기능 덮어쓰기 delete 기능 덮어쓰기 예제
> 스프링 데이터 JPA는 항상 내가 Custom하게 구현한 구현체를 우선순위를 높게 줌  

#### PostCustomRepository 인터페이스에 delete 추가
```java
public interface PostCustomRepository<T> {
    void delete(T entity);
}
```

#### delete 메서드 기능 구현
```java
@Repository
@Transactional
public class PostCustomRepositoryImpl implements PostCustomRepository<Post>{

    @Autowired
    EntityManager entityManager;

    @Override
    public void delete(Post entity) {
        System.out.println("custom delete");
        entityManager.remove(entity);
    }
}
```

#### PostRepository 타입 추가
```java
public interface PostRepository extends JpaRepository<Post, Long>, PostCustomRepository<Post> {
}
```

#### 테스트 로직 구현
> 아래와 같이 구현하면 Hibernate가 판단하여 쿼리를 수행하지 않음  
```java
@RunWith(SpringRunner.class)
@DataJpaTest
public class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @Test
    public void crud() {
        Post post = new Post();
        post.setTitle("hibernate");
        postRepository.save(post);
        postRepository.delete(post);
    }
}
```

#### Insert와 Select가 일어나도록 구현
> 아래와 같이 구현하면 select가 필요하다고 판단하여 플러싱이 일어나서 데이터가 Insert 되고  
> select하여 데이터를 가져옴  
> delete는 객체상태에서 entityManager가 removed 상태로 만들었지만 실제로 데이터베이스에 sync는 하지 않는다  
> 하지만 기본적으로 `@Transactional`이 붙어있는 스프링의 모든 테스트는 그 트랜잭션을 모두 ROLLBACK 트랜잭션으로 간주함  
> Hibernate의 경우 ROLLBACK 트랜잭션의 경우 필요없는 Query는 아예 날리지 않음  
> 강제로 flush() 하면 delete Query를 날림  
```java
@RunWith(SpringRunner.class)
@DataJpaTest
public class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @Test
    public void crud() {
        Post post = new Post();
        post.setTitle("hibernate");
        postRepository.save(post);
        postRepository.findMyPost();
        postRepository.delete(post);
        postRepository.flush();
    }
}
```
 
## 접미어 설정하기 
> 기본적으로 repositoryImplementationPostfix의 접미어는 `impl` 인데 변경하고 싶다면 아래와 같이 설정하면 된다  
```java
@SpringBootApplication
@EnableJpaRepositories(repositoryImplementationPostfix = "Default")
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```


## JPA DELETE에 대하여
- detached 상태: 한번 언젠가 persistence 상태가 되었던 인스턴스가 persistence 상태에서 빠진 상태
  - 더이상 PersistentContext에서 관리를 받지않는 대상이된 객체
  - Transaction 밖으로 나옴
  - Transaction 이 끝나고 계속해서 남아있는 레퍼런스
  - 세션을 클리어 함
  - 예전에 Persistence 상태였다는 말은 맵핑되는 테이블에 데이터가 있다는 말임  
> detached 상태의 인스턴스를 예전에 맵핑이 됐던 인스턴스이므로 `merge`하면서 다시 persistentContext로 로딩을 해오면서 Sync를 맞춤  
> 그리고 entityManager가 해당 객체를 삭제하여 removed 상태로 만듬  
> cascade 처럼 entityManager로 로딩해서 처리하는 이유가 있음 단순히 성능적인 부분만 고려하면 안됨  