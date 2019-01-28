# 스프링 데이터 JPA: Named Parameter과 SpEL
## Named Parameter
> `@Query`에서 참조하는 매개변수를 `?1`, `?2` 이렇게 채번으로 참조하는게 아니라  
> 이름으로 `:title` 이렇게 참조하는 방법은 다음과 같습니다  
```java
@Query("SELECT p FROM Post AS p WHERE p.title = :title")
List<Post> findByTitle(@Param("title") String title, Sort sort);
```
 
## SpEL(Spring Expression Language)
https://docs.spring.io/spring/docs/current/spring-framework-reference/core.html#expressions
> 스프링 표현식 언어로 문자열을 처리해줌  
> `@Query`에서 엔티티 이름을 미리정의가 되어있는 `#{#entityName}` 으로 표현할 수 있습니다  
```java
@Query("SELECT p FROM #{#entityName} AS p WHERE p.title = :title")
List<Post> findByTitle(@Param("title") String title, Sort sort);
```