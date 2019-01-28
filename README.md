# 스프링 데이터 JPA: Query by Example
- QBE는 필드 이름을 작성할 필요 없이(뻥)
- 단순한 인터페이스를 통해 동적으로 쿼리를 만드는 기능을 제공하는 사용자 친화적인 쿼리 기술입니다
- 어떠한 예제 객체를 가지고 쿼리를 만드는 개념
- 잘 사용하지 않게 될 것 같은 기능
 
## Example = Probe + ExampleMatcher
- Probe는 필드에 어떤 값들을 가지고 있는 도메인 객체
- ExampleMatcher는 Prove에 들어있는 그 필드의 값들을 어떻게 쿼리할 데이터와 비교할지 정의한 것
- Example은 그 둘을 하나로 합친 것. 이걸로 쿼리를 함
 
## 장점
- 별다른 코드 생성기나 애노테이션 처리기 필요 없음.
- 도메인 객체 리팩토링 해도 기존 쿼리가 깨질 걱정하지 않아도 됨.(뻥)
- 데이터 기술에 독립적인 API

## 단점
- nested 또는 프로퍼티 그룹 제약 조건을 못 만든다.
- 조건이 제한적이다 
- 문자열은 starts/contains/ends/regex 가 가능하고 그밖에 propery는 값이 정확히 일치해야 한다
 
## QueryByExampleExecutor
https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#query-by-example

## 실습
#### Repository 에 QueryByExampleExecutor 추가
```java
public interface CommentRepository extends JpaRepository<Comment, Long>, QueryByExampleExecutor<Comment> {
}
```

#### 테스트 작성
> 다른조건은 무시하고 best가 true인 예제를 만들고 쿼리를 수행  
```java
@Test
public void qbe() {
    Comment prove = new Comment();
    prove.setBest(true);

    ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny().withIgnorePaths("up","down");
    Example<Comment> example = Example.of(prove, exampleMatcher);
    comments.findAll(example);
}
```