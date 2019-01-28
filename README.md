# 스프링 데이터 Common: Repository 인터페이스 정의하기
## Repository 인터페이스로 공개할 메소드를 직접 일일히 정의하고 싶다면
> JpaRepository 에서 들어오는 기능들이 싫고 직접 정의하고 싶다면 두가지 방법이 있음  

### 특정 리포지토리 당 @RepositoryDefinition
> 내가 사용할 기능을 내가 직접 메서드를 정의  
> 메서드에 해당하는 기능을 스프링 데이터 JPA가 구현할 수 있는 것이라면 구현해서 기본으로 제공해줌  
```java
@RepositoryDefinition(domainClass = Comment.class, idClass = Long.class)
public interface CommentRepository {
    Comment save(Comment comment);
    List<Comment> findAll();
}
```

### 공통 인터페이스 정의 @NoRepositoryBean
```java
@NoRepositoryBean
public interface MyRepository<T, ID extends Serializable> extends Repository<T, ID> {
    <E extends T> E save(E entity);
    List<T> findAll();
    long count();
}
```

#### 정의한 공통 인터페이스를 상속받아서 사용
```java
public interface CommentRepository extends MyRepository<Comment, Long>{
}
```

### 테스트 실습
```java
@RunWith(SpringRunner.class)
@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Test
    public void crud() {
        Comment comment = new Comment();
        comment.setComment("Hello Comment");
        commentRepository.save(comment);

        List<Comment> all = commentRepository.findAll();
        assertThat(all.size()).isEqualTo(1);

        long count = commentRepository.count();
        assertThat(count).isEqualTo(1);
    }
}
```