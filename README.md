# JPA 프로그래밍: Fetch
## Fetch
> 연관 관계의 엔티티의 정보를 지금(Eager) 나중에(Lazy) 가져올지 설정  
> 잘 조정해야 성능을 향상시킬 수 있음  
- @OneToMany의 기본값은 Lazy:
  - 기본적으로 해당 Entity의 정보를 가져올때 Lazy가 적용된 @OneToMany 관계의 Entity의 정보를 가져오지는 않음
  - 얼마나 많이 있을 지도 모르고 사용하지도 않을 값들을 다 가져오면 객체에 불필요한 정보를 로딩할 수도 있으므로
- @ManyToOne의 기본값은 Eager: 해당 Entity의 정보를 가져올때 Eager로 설정된 @ManyToOne 관계의 Entity의 정보도 같이 가져옴

### Eager 테스트
> EAGER가 적용된 Entity 정보를 미리 다 가져와서 불필요한 조회를 더이상 하지 않음  
#### Account 클래스의 studies에 EAGER 를 적용
```java
@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
private Set<Study> studies = new HashSet<>();
```

#### JpaRunner 에서 테스트
```java
Session session = entityManager.unwrap(Session.class);
Post post = session.get(Post.class, 4l);
System.out.println("========================");
System.out.println(post.getTitle());
```

### Lazy
> Lazy가 적용된 Entity 정보를 미리 가져 오지 않음  
#### Account 클래스의 studies에 LAZY를 적용
```java
@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
private Set<Study> studies = new HashSet<>();
```

### n+1 테스트
- 재현되지 않음 
- fetch에 Lazy 옵션이 적용되어 있더라도 n에 해당되는 것을 한번에 다가져와서 처리하므로 재현되지 않음
- Hibernate의 기능 개선으로 성능상의 이슈가 크게 없을 것으로 보임
- 너무 많은 데이터를 객체에 로딩하는 문제는 있을 수 있음
```java
// EAGER 테스트
Session session = entityManager.unwrap(Session.class);
Post post = session.get(Post.class, 4l);
System.out.println("========================");
System.out.println(post.getTitle());

post.getComments().forEach(c -> {
    System.out.println("--------------");
    System.out.println(c.getComment());
});
```

## Hibernate 
- load: 가져오려 할때 없으면 예외를 던짐 Proxy로도 가져올 수 있음
- get: 무조건 DB에서 가져옴 해당하는게 없으면 예외를 던지지 않고 무조건 레퍼런스를 null로 만듬