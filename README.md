# 스프링 데이터 JPA: 마무리

## @Enumerated 맵핑
#### @Enumerated 로 선언해서 맵핑
> 기본값이 `EnumType.ORDINAL` 로 되어있는데 순서가 바뀌면 큰일이 나기 때문에 아주 위험하다  
> `EnumType.STRING` 로 변경해주어야 안전하다  
```java
@Entity
public class Comment {
    @Enumerated(value = EnumType.STRING)
    private CommentStatus commentStatus;
}
```

#### enum 생성
```java
public enum CommentStatus {
    DRAFT, DELETED, PUBLISHED
}
```