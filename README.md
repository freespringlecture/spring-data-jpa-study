# 스프링 데이터 JPA: Update 쿼리 메소드
> 쿼리 메서드에서 생성할때 @Query 없이 count나 delete도 사용할 수 있음  
> Update는 PersistenceContext가 persist 상태의 객체를 관리 하다가 
> 객체상태의 변화가 일어났고 이 변화를 데이터베이스에 sync를 해야겠다는 시점에 flush라는 것을 함  
> 그때 보통 Update 쿼리를 자동으로 만들어서 실행함  
## 쿼리 생성하기
- find...
- count...
- delete...
- 흠.. update는 어떻게 하지?
 
## Update 또는 Delete 쿼리 직접 정의하기
#### @Modifying @Query
> 추천하진 않습니다  
```java
@Modifying(clearAutomatically = true, flushAutomatically = true)
@Query("UPDATE Post p SET p.title = ?2 WHERE p.id = ?1")
int updateTitle(Long id, String title);
```

#### 테스트 코드
```java
private Post savePost() {
    Post post = new Post();
    post.setTitle("Spring");
    return postRepository.save(post);
}

@Test
public void updateTitle() {
    Post spring = savePost();

    String hibernate = "hibernate";
    int update = postRepository.updateTitle(hibernate, spring.getId());
    assertThat(update).isEqualTo(1);

    // spring 객체의 title은 여전히 spring 
    Optional<Post> byId = postRepository.findById(spring.getId());
    assertThat(byId.get().getTitle()).isEqualTo(hibernate);
}
```

## 추천하지 않는 이유
> 트랜잭션이 아직 끝나지 않고 캐시가 비워지지 않았기 때문에 spring 객체가 PersistenceContext에 그대로 있어서  
> spring 객체는 계속해서 persist 상태의 객체임  
> persist 상태의 객체는 1차 캐시 즉, PersistenceContext가 관리하고 있음  
> 그 상태에서 find를 하면 DB를 가지 않음 자기자신이 캐싱하고 있던 것을 그대로 가지고 옴  
> 그래서 비록 데이터베이스의 Update 쿼리가 발생했지만 spring 객체는 persist 상태의 객체이므로 캐시에 남아있었기 떄문에  
> title은 여전히 spring임  
  
> DELETE의 경우 DELETE 할때 도메인 이벤트를 잡아서 사용할 수 있는 콜백이 있는데 그런 콜백이 적용이 안됨  

### 캐시문제를 해결하기 위한 @Modifying 옵션
```java
@Modifying(clearAutomatically = true, flushAutomatically = true)
```
#### clearAutomatically 
- clear: 그동안 persistence context에 쌓여있던 캐시를 비워주는 것  
> Update 쿼리를 실행한 다음에 persistence context 상태를 clear 해주는 옵션  
> Update 쿼리를 실행한 다음에 persistence context 상태를 비워줘야 find 할때 다시 새로 읽어옴  

#### flushAutomatically
- flush: 그동안 persistence context에 쌓여있던 변경사항을 데이터베이스에 업데이트 해주는 것  
> Update 쿼리를 실행하기 전에 persistence context 상태를 flush 해주는 옵션  

## 권장하는 방법
> 명시적으로 Update 쿼리를 실행하지는 않았지만  
> find 하기전에 DB에 sync를 해야한다는 사실을 hibernate가 알고 있으므로 select 하기 전에 데이터베이스에 반영을 해줌  
```java
Post spring = savePost();
spring.setTitle("hibernate");

List<Post> all = postRepository.findAll();
assertThat(all.get(0).getTitle()).isEqualTo("hibernate");
```