# 스프링 데이터 JPA: 쿼리 메소드
## 쿼리 생성하기
https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation
### 스프링 데이터 JPA 에서 지원하는 키워드
- And, Or
- Is, Equals
- LessThan, LessThanEqual, GreaterThan, GreaterThanEqual
- After, Before
- IsNull, IsNotNull, NotNull
- Like, NotLike
- StartingWith, EndingWith, Containing
- OrderBy
- Not, In, NotIn
- True, False
- IgnoreCase
 
## 쿼리 찾아쓰기 
- 엔티티에 정의한 쿼리 찾아 사용하기 JPA Named 쿼리
  > 미리 JPQL, Native 쿼리를 정의 해놓고 그 쿼리를 정의한 Name을 Lookup해서 사용하는 방법  
  > 해당 Entity 상단에 정의  
  - @NamedQuery
  ```java
  @Entity
  @NamedQuery(name = "Post.findByTitle", query = "SELECT p FROM Post AS p WHERE p.title = ?1")
  ```
  - @NamedNativeQuery
- 리포지토리 메소드에 정의한 쿼리 사용하기
  > @NamedQuery를 사용하면 Entity가 지저분해 지므로 @Query를 권장  
  > 메서드 위에 정의  
  - @Query  
  ```java
  @Query("SELECT p FROM Post AS p WHERE p.title = ?1")
  List<Post> findByTitle(String title);
  ```
  - @Query(nativeQuery=true)  
  ```java
  @Query(value = "SELECT p FROM Post AS p WHERE p.title = ?1", nativeQuery = true)
  List<Post> findByTitle(String title);
  ```
