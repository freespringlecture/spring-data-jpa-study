# 스프링 데이터 JPA: Projection
> 엔티티의 일부 데이터만 가져오기  
```sql
SELECT c.id, c.comment FROM Comment c;
```
 
## 인터페이스 기반 프로젝션
- Nested 프로젝션 가능
- Closed 프로젝션
  - 쿼리를 최적화 할 수 있다. 가져오려는 애트리뷰트가 뭔지 알고 있으니까
  - Java 8의 디폴트 메소드를 사용해서 연산을 할 수 있다
- Open 프로젝션  
> 다 가져온다음 그중에서 내가 보고싶은 것만 조합을 하거나 연산을 해서 보는 것  
  - @Value(SpEL)을 사용해서 연산을 할 수 있다. 스프링 빈의 메소드도 호출 가능
  - 쿼리 최적화를 할 수 없다. SpEL을 엔티티 대상으로 사용하기 때문에
 
## 클래스 기반 프로젝션
- DTO
- Lombok @Value로 코드 줄일 수 있음
 
## 다이나믹 프로젝션
> 프로젝션 용 메소드 하나만 정의하고 실제 프로젝션 타입은 타입 인자로 전달하기  
```java
<T> List<T> findByPost_Id(Long id, Class<T> type);
```

## Closed 프로젝션 실습
#### Comment Entity에 변수 추가
```java
private int up;
private int down;
private boolean best;
```

#### CommentSummary 인터페이스 추가
```java
public interface CommentSummary {
    String getComment();
    int getUp();
    int getDown();
}
```

#### CommentRepository 에 기본 쿼리 메서드 추가
```java
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByPost_Id(Long id);
}
```

#### 테스트 코드
> 기본적인 쿼리 메서드로 호출하면 컬럼을 모두 다 가져오는 것을 볼 수 있음  
```java
@RunWith(SpringRunner.class)
@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    CommentRepository comments;

    @Test
    public void getComment() {
        comments.findByPost_Id(1l);
    }
}
```

#### CommentRepository 에 기본 쿼리 메서드 타입을 CommentSummary로 변경
> Closed(한정적인) 프로젝션 방식으로 가져오려고 했던 컬럼들만 가져옴  
```java
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<CommentSummary> findByPost_Id(Long id);
}
```

## Open 프로젝션 실습
#### CommentSummary에 open 프로젝션 인터페이스 추가
> target의 up과 down을 합쳐서 getVotes() 라는 것으로 가져오겠다는 인터페이스 추가  
> target는 comment인데 한정짓지 못해서 전부다 가져오게됨 그래서 open 프로젝션이라고 부르게 됨  
```java
@Value("#{target.up + ' ' + target.down}")
String getVotes();
```

#### 테스트 코드
```java
Post post = new Post();
post.setTitle("jpa");
Post savedPost = posts.save(post);

Comment comment = new Comment();
comment.setComment("spring data jpa projection");
comment.setPost(savedPost);
comment.setUp(10);
comment.setDown(1);
comments.save(comment);

comments.findByPost_Id(savedPost.getId()).forEach(c -> {
    System.out.println("========================");
    System.out.println(c.getVotes());
});
```

## Open 프로젝션의 장점을 살리면서 Closed 프로젝션 으로 쿼리 실습
> Java8 부터 인터페이스에 default 메서드를 사용할 수 있게 되었으므로  
> Custom한 구현체를 만들어서 메서드를 추가할 수도 있고 그러면서 사용할 필드들은 한정적으로 되니까  
> 쿼리는 최적화가 되고 Custom한 계산이나 조합이 가능해서 가장 좋은 방법임  
```java
default String getVotes() {
    return getUp() + " " + getDown();
}
```


## 클래스 기반 실습
> 클래스 기반으로도 동일하게 동작하게 만들 수 있음  
#### CommentSummary 클래스 추가
```java
public class CommentSummary {

    private String comment;
    private int up;
    private int down;

    public CommentSummary(String comment, int up, int down) {
        this.comment = comment;
        this.up = up;
        this.down = down;
    }

    public String getComment() {
        return comment;
    }

    public int getUp() {
        return up;
    }

    public int getDown() {
        return down;
    }

    public String getVotes() {
        return this.up + " " + this.down;
    }
}
```

## 다이나믹 프로젝션 실습
> 프로젝션 용 메소드 하나만 정의하고 실제 프로젝션 타입은 타입 인자로 전달하기  
> Repository에 쿼리 메서드를 더 추가하지 않고 여러가지 다양한 프로젝션을 사용할 수 있음  

#### CommentOnly 인터페이스 추가
```java
public interface CommentOnly {
    String getComment();
}
```

#### CommentRepository findByPost_Id 제네릭 타입으로 변경
> 어떤 타입의 프로젝션을 쓰는지 타입을 줘야함  
```java
//List<CommentSummary> findByPost_Id(Long id);
<T> List<T> findByPost_Id(Long id, Class<T> type);
```

#### 테스트 로직
```java
Post post = new Post();
post.setTitle("jpa");
Post savedPost = posts.save(post);

Comment comment = new Comment();
comment.setComment("spring data jpa projection");
comment.setPost(savedPost);
comment.setUp(10);
comment.setDown(1);
comments.save(comment);

comments.findByPost_Id(savedPost.getId(), CommentSummary.class).forEach(c -> {
    System.out.println("========================");
    System.out.println(c.getVotes());
});
```

#### CommentOnly 프로젝션 테스트
> CommentSummary 프로젝션을 CommentOnly로 변경하고 테스트  
```java
comments.findByPost_Id(savedPost.getId(), CommentOnly.class).forEach(c -> {
    System.out.println("========================");
    System.out.println(c.getComment());
});
```