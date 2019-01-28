# 스프링 데이터 JPA: EntityGraph
> 쿼리 메소드 마다 연관 관계의 Fetch 모드를 유연하게 설정 할 수 있음  

## Fetch 모드 실습
- Comment에 Post와 연관관계 @ManyToOne 설정하면
- @ManyToOne 의 경우 Fetch 모드 기본값이 EAGER @ManyToOne(fetch = FetchType.EAGER)
- 끝이 Many로 끝나면 기본값이 LAZY
- EAGER의 경우 comment 정보를 가져올때 post 정보도 미리 가져옴
- LAZY로 설정하면 comment 정보만 가져오고 post 데이터가 필요한 시점에 쿼리가 다시 날아감
- 쿼리를 보내려면 persistence context 가 관리하는 상태여야지 가능함  
> 즉, comment 인스턴스가 persist 상태여야지 가능함 트랜잭션 안에 있거나 캐시를 clear하지 않았으면 persist 상태임  
#### Comment 클래스 생성
```java
@Entity
public class Comment {

    @Id @GeneratedValue
    private Long id;

    private String comment;

    @ManyToOne
    private Post post;
}
```

#### CommentRepository 인터페이스 생성
```java
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
```

#### 테스트 코드
> Comment를 가져올때 Select 하면서 Post 데이터도 같이 가져옴  
```java
@RunWith(SpringRunner.class)
@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Test
    public void getComment() {
        commentRepository.findById(1l);
    }
}
```

## 왜 필요한지?
> 필요한 경우에 따라서 다른 Fetch 모드가 필요한 경우가 있을 때  
 
## @NamedEntityGraph
> @Entity에서 재사용할 여러 엔티티 그룹을 정의할 때 사용  
> @NamedEntityGraph를 여러개 정의 할 수도 있음  

### 실습
#### 연관관계 정의
> Entity 위에 연관관계 정의  
```java
@NamedEntityGraph(name = "Comment.post", attributeNodes = @NamedAttributeNode("post"))
@Entity
```
 
## @EntityGraph
> @NamedEntityGraph에 정의되어 있는 엔티티 그룹을 사용 해서 실제로 동작하는 로직을 구현  
> 각각의 메서드마다 다른 Fetching 전략으로 데이터를 읽어올 수 있도록 여러가지 메서드를 만들 수 있음  

### 실습
> @NamedEntityGraph에 정의된 그룹의 이름 Comment.post를 정의하면  
> @NamedAttribute에 정의된 post를 기본값인 EAGER 모드로 가져옴  
```java
public interface CommentRepository extends JpaRepository<Comment, Long> {
    @EntityGraph(value = "Comment.post")
    Optional<Comment> getById(Long id);
}
```

#### 테스트 
- @EntityGraph로 정의한 getById를 사용하여 조회한 결과는 EAGER 모드가 적용되어 post를 같이 조회해옴  
- 스프링 데이터 JPA가 제공해주는 findById의 경우에는 기본 패칭 전략이 적용되어 LAZY로 가져옴  
- 경우에 따라서 원하는 방식으로 Fetching 전략을 선택해서 가져올 수 있게 됨  
```java
@RunWith(SpringRunner.class)
@DataJpaTest
public class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;

    @Test
    public void getComment() {
        commentRepository.getById(1l);

        System.out.println("=================================");

        commentRepository.findById(1l);
    }
}
```
- 그래프 타입 설정 가능
  - (기본값) FETCH: 설정한 엔티티 애트리뷰트는 EAGER 패치 나머지는 LAZY 패치
  - LOAD: 설정한 엔티티 애트리뷰트는 EAGER 패치 나머지는 기본 패치 전략 따름

### 간단한 사용예시
> 아래와 같이 @NamedEntityGraph에 정의한 name이 아니라 Entity 네임을 바로 지정할 수도 있음  
> 배열로 가져올 수도 있음  
```java
@EntityGraph(attributePaths = "post")
```