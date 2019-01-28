# 스프링 데이터 Common: 쿼리 만들기 개요

## 스프링 데이터 저장소의 메소드 이름으로 쿼리 만드는 방법
### 1. 메소드 이름을 분석해서 ​쿼리 만들기​ (CREATE)
> 메서드 이름을 분석해서 스프링 데이터 JPA(Common)가 자동으로 쿼리를 만들어 준다  
```java
public interface CommentRepository extends MyRepository<Comment, Long>{
    //Comment title에 Keyword가 들어있는 모든 Comment를 찾아주는 메서드
    List<Comment> findByCommentContains(String Keyword);
}
```
### 2. 미리 정의해 둔 ​쿼리 찾아 사용하기​ (USE_DECLARED_QUERY)
- 정의하거나 구현하는 방법이 구현체 마다 다름
- JPA를 사용하고 있기 때문에 JPQL을 기본값으로 받아들여서 사용
- nativeQuery(SQL)를 사용하고 싶다면 nativeQuery라고 명시(nativeQuery = true)하고 사용하면 됨
```java
public interface CommentRepository extends MyRepository<Comment, Long>{
    //Comment title에 Keyword가 들어있는 모든 Comment를 찾아주는 메서드
    @Query("SELECT c FROM Comment AS c")
    List<Comment> findByCommentContains(String Keyword);
}
```

### 3. 미리 정의한 쿼리 찾아보고 없으면 만들기 (CREATE_IF_NOT_FOUND)
- 선언이 되어있는 쿼리를 찾아보고 선언이 된 쿼리가 없는 경우 메서드 이름을 분석해서 생성함
- 별다른 설정을 하지 않으면 `@EnableJpaRepositories`에 기본으로 설정되어있기 때문에 생략해도됨
```java
@SpringBootApplication
@EnableJpaRepositories(queryLookupStrategy = QueryLookupStrategy.Key.CREATE_IF_NOT_FOUND)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

## 쿼리 만드는 방법
- 리턴타입 {접두어}{도입부}By{프로퍼티 표현식}(조건식)[(And|Or){프로퍼티 표현식}(조건식)]{정렬 조건} (매개변수)
  
| 항목            | 명령어                                                       |
| --------------- | ------------------------------------------------------------ |
| 접두어          | Find, Get, Query, Count, ...                                 |
| 도입부          | Distinct, First(N), Top(N)                                   |
| 프로퍼티 표현식   | Person.Address.ZipCode => find(Person)ByAddress_ZipCode(...) |
| 조건식          | IgnoreCase, Between, LessThan, GreaterThan, Like, Contains, ... |
| 정렬 조건       | OrderBy{프로퍼티}Asc                                         |
| 리턴 타입       | E, Optional<E>, List<E>, Page<E>, Slice<E>, Stream<E>        |
| 매개변수        | Pageable, Sort                                               |
  
### 예제
> 페이징으로 받고 싶으면 Pageable 파라메터를 줘야지 Pageable에 들어오는 페이징 정보 파라메터를 사용해서  
> 페이징하는 쿼리를 작성해서 페이지에 담아올 수 있음 List로 받을 수 있지만 페에징 정보가 누락됨  
```java
//어떠한 post에 들어가 있으면서 그중에서 like가 몇개 이상인 Comment를 조회해오는 메서드
Page<Comment> findByLikeCountGreaterThanAndPost(int likeCount, Post post, Pageable pageable);
```

#### Sorting 예시
```java
//최근 생성일 순으로 DESC 정렬해서 TOP 10개만 조회하는 Sorting 적용 예시
Page<Comment> findTop10ByLikeCountGreaterThanAndPostOrderByCreatedDesc(int likeCount, Post post, Pageable pageable);
```

#### 권장하는 Sorting 방법
> Pageable에 Sort메서드가 들어있어 Pageable로 Sorting 하는 것을 권장  
```java
Page<Comment> findByLikeCountGreaterThanAndPost(int likeCount, Post post, Pageable pageable);
```

#### List Sorting 예시
```java
List<Comment> findByLikeCountGreaterThanAndPost(int likeCount, Post post, Sort sort);
```
## 쿼리 찾는 방법
- 메소드 이름으로 쿼리를 표현하기 힘든 경우에 사용.
- 저장소 기술에 따라 다름.
- JPA: @Query @NamedQuery
- 적용되는 순서가 있다 @Query -> @NamedQuery

## 디버깅
- JPA가 만들지 못하는 쿼리인 경우 오류가 발생함
- 정의 되지 않은 변수를 선언해서 오류 Title -> Comment로 변경
- primitive int로 정의 한경우 not null 이 적용됨
- Integer로 정의할경우 null 허용
- Like의 경우 예약어라 오류 likeCount로 변경