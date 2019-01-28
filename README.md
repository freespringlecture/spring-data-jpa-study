# 스프링 데이터 Common: 기본 리포지토리 커스터마이징
> 모든 리포지토리에 공통적으로 추가하고 싶은 기능이 있거나 덮어쓰고 싶은 기본 기능이 있다면   
 
1. JpaRepository를 상속 받는 인터페이스 정의
   - @NoRepositoryBean
2. 기본 구현체를 상속 받는 커스텀 구현체 만들기
3. @EnableJpaRepositories에 설정
   - repositoryBaseClass

## 실습
> 어떤 Entity가 PersistentContext에 들어있는 지 확인하는 기능 구현  
#### JpaRepository를 상속받는 MyRepository 구현
> 중간에 들어가는 Repository는 반드시 @NoRepositoryBean을 등록해줘야함  
```java
@NoRepositoryBean
public interface MyRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {
    boolean contains(T entity);
}
```

#### SimpleJpaRepository 구현
- 스프링 데이터 JPA가 제공해주는 가장많은 기능을 가지고 있는 가장 밑단에 있는 클래스
- JpaRepository를 상속받으면 가져오게 되는 구현체
- MyRepository도 상속 받음
- 부모에 super를 호출해야되므로 두개의 인자를 받는 생성자도 만들어줘야함
- 빈으로 주입하지 않고 사용자에게 전달을 받는 entityManager를 사용 
```java
public class SimpleMyRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements MyRepository<T, ID> {
 
    private EntityManager entityManager;
 
    public SimpleMyRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }
 
    //entity를 전달 받아 PersistentContext 안에 들어 있는지 검증하는 로직 구현
    @Override
    public boolean contains(T entity) {
        return entityManager.contains(entity);
    }
}
```

#### repositoryBaseClass로 설정
```java
@EnableJpaRepositories(repositoryBaseClass = SimpleMyRepository.class)
```

#### PostRepository가 MyRepository를 상속받도록 구현
```java
public interface PostRepository extends MyRepository<Post, Long> {
}
```

#### 테스트 로직 구현
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

        // save 하기 전에 postRepository.contains안에 post가 들어있는지 검증
        // post가 들어있지 않은 Transient 상태
        assertThat(postRepository.contains(post)).isFalse();

        postRepository.save(post);

        // post가 들어있는 Persist 상태
        assertThat(postRepository.contains(post)).isTrue();

        postRepository.delete(post);
        postRepository.flush();
    }
}
```

## JpaRepository에서 제공하는 다른 기능을 커스터마이징하고 싶다면?
> 구현체인 SimpleMyRepository에서 JpaRepository의 특정기능을 Overriding 해서 재구현하면 됨  
```java
public class SimpleMyRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements MyRepository<T, ID> {

    private EntityManager entityManager;

    public SimpleMyRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public List<T> findAll() {
        return super.findAll();
    }
}
```