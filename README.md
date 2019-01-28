# 스프링 데이터 Common: 비동기 쿼리
> 백그라운드에서 동작하는 threadpool에 메서드 실행하는 작업을 위임하는 것  
> 메서드 호출해서 실행하는 것을 별도의 스레드에서 동작시킴  

## 비동기 쿼리
- Future: Java5에 추가됨  
  > 정의 하는 부분까지는 논블로킹이지만 get으로 사용할 때는 블로킹이라 애매함  
- CompletableFuture: Java8에 추가됨
- ListenableFuture: Spring에서 만든것 제일 깔끔함  
  > 모두 Async로 구현할 수 있음  
```java
@Async Future<User> findByFirstname(String firstname);               
@Async CompletableFuture<User> findOneByFirstname(String firstname); 
@Async ListenableFuture<User> findOneByLastname(String lastname); 
```
- 해당 메소드를 스프링 TaskExecutor에 전달해서 별도의 쓰레드에서 실행함
- Reactive랑은 다른 것임

## 비동기 쿼리 실습1
> 아래와 같이 그냥 @Async 애노테이션만 추가한다고 Async로 처리되는게 아님  
> 스프링 부트에서는 @EnableAsync를 설정하고 비동기설정을 해야하지만 너무 번거롭고 테스트가 힘듬  
#### @Async 애노테이션 추가하고 Future로 리턴
```java
public interface CommentRepository extends MyRepository<Comment, Long>{
    @Async
    Future<List<Comment>> findByCommentContainsIgnoreCase(String keyword, Pageable pageable);
}
```

#### 테스트 코드
- future.isDone() 결과가 나왔는지 확인 할 수 있음
- future.get은 결과가 나올때까지 무작정 기다림
- future.get 중 timeout을 주고 정해진 시간만큼 기다리는 메서드도 있음
```java
@RunWith(SpringRunner.class)
@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Test
    public void crud() throws ExecutionException, InterruptedException {
        this.createComment(100, "spring data jpa");
        this.createComment(55, "HIBERNATE SPRING");

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "LikeCount"));

        Future<List<Comment>> future = commentRepository.findByCommentContainsIgnoreCase("Spring", pageRequest);
        System.out.println("===============");
        System.out.println("is done?" + future.isDone());
        List<Comment> comments = future.get();
        comments.forEach(System.out::println);
    }

    private void createComment(int likeCount, String comment) {
        Comment newComment = new Comment();
        newComment.setLikeCount(likeCount);
        newComment.setComment(comment);
        commentRepository.save(newComment);
    }
}
```

## 비동기 쿼리 실습2
#### @EnableAsync 설정
```java
@SpringBootApplication
@EnableAsync
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

#### ListenableFuture 로 리턴 하도록 변경
```java
public interface CommentRepository extends MyRepository<Comment, Long>{
    @Async
    ListenableFuture<List<Comment>> findByCommentContainsIgnoreCase(String keyword, Pageable pageable);
}
```

#### ListenableFuture로 비동기 처리 하도록 구현
```java
ListenableFuture<List<Comment>> future = commentRepository.findByCommentContainsIgnoreCase("Spring", pageRequest);
System.out.println("===============");
System.out.println("is done?" + future.isDone());

future.addCallback(new ListenableFutureCallback<List<Comment>>() {
    @Override
    public void onFailure(Throwable ex) {
        System.out.println(ex);
    }

    @Override
    public void onSuccess(@Nullable List<Comment> result) {
        System.out.println("================");
        result.forEach(System.out::println);
    }
});
```

### 플러싱 처리 추가
> 아래와 같이 해도 정상적인 처리를 못함  
#### JpaRepository를 상속받도록 변경
```java
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Async
    ListenableFuture<List<Comment>> findByCommentContainsIgnoreCase(String keyword, Pageable pageable);
}
```

#### 기존로직에 flush 처리
```java
this.createComment(100, "spring data jpa");
this.createComment(55, "HIBERNATE SPRING");
commentRepository.flush();

List<Comment> all = commentRepository.findAll();
assertThat(all.size()).isEqualTo(2);
```

#### Thread.sleep으로 응답을 기다리는 로직 추가
```java
future.addCallback(new ListenableFutureCallback<List<Comment>>() {
    @Override
    public void onFailure(Throwable ex) {
        System.out.println(ex);
    }

    @Override
    public void onSuccess(@Nullable List<Comment> result) {
        System.out.println("===== Async =====");
        System.out.println(result.size());
    }
});

Thread.sleep(5000l);
```

## 실습 코드의 문제점
1. 플러싱 - 내가 원하는 타이밍에 데이터를 보내야함
2. 언제 끝날지 모르는 thread는 Thread.sleep로 기다리거나 명시적으로 get을 호출해서 기다려서 가져오거나
3. @transactinal 문제 원래 thread Data는 Select할 수있지만 새롭게 조작중인 thread Data는 Select 못함
 
## 권장하지 않는 이유
- 테스트 코드 작성이 어려움
- 코드 복잡도 증가
- 리엑티브를 지원하는 데이터베이스 JDBC가 아직 없음
- 성능상 이득이 없음
  - DB 부하는 결국 같고
  - 메인 쓰레드 대신 백드라운드 쓰레드가 일하는 정도의 차이
  - 단, 백그라운드로 실행하고 결과를 받을 필요가 없는 작업이라면 @Async를 사용해서 응답 속도를 향상 시킬 수는 있다
- 위의 비동기 기능은 사용하지 말고 스프링5 webflux 사용을 권장
- 리엑티브기반의 코딩을 하고 싶다면 MongoDB 같은 리엑티브를 지원하는 NoSQL을 사용해야함  
  > NoSQL의 경우에는 적은 Thread 개수로 높은 성능을 냄  