# 스프링 데이터 JPA: 쿼리 메소드 Sort
> 이전과 마찬가지로 Pageable이나 Sort를 매개변수로 사용할 수 있는데, @Query와 같이 사용할 때 제약 사항이 하나 있습니다  
> Order by 절에서 함수를 호출하는 경우에는 Sort를 사용하지 못합니다. 그 경우네는 JpaSort.unsafe()를 사용 해야 합니다  
- Sort는 그 안에서 사용한 프로퍼티 또는 alias가 엔티티에 없는 경우에는 예외가 발생합니다
- `JpaSort.unsafe()`를 사용하면 함수 호출을 할 수 있습니다
  ```java
  JpaSort.unsafe(“LENGTH(firstname)”);
  ```
## Sort 예제
```java
import org.springframework.data.domain.Sort;

@Query("SELECT p FROM Post AS p WHERE p.title = ?1")
List<Post> findByTitle(String title, Sort sort);
```

#### 테스트 코드
```java
List<Post> all = postRepository.findByTitle("Spring", Sort.by("title"));
```

#### 프로퍼티 또는 alias 가 아닌 경우 오류
```java
Sort.by("LENGTH(title)")
```

## JpaSort.unsafe()
> `JpaSort.unsafe()`를 사용하면 함수를 호출 한 결과로 정렬하는 것도 가능함  
```java
JpaSort.unsafe(“LENGTH(title)")
```

## alias 예제
> `p.title AS pTitle` 처럼 alias를 줄 수 있음  
```java
@Query("SELECT p, p.title AS pTitle FROM Post AS p WHERE p.title = ?1")
List<Post> findByTitle(String title, Sort sort);
```