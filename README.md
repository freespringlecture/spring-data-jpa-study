# JPA 프로그래밍 7: Query
> JPA, Hibernate를 사용할 때는 항상 무슨 Query를 발생시키는지 그게 의도한 것인지 확인해야됨  

## JPQL (HQL)
- Java Persistence Query Language / Hibernate Query Language
- 데이터베이스 테이블이 아닌, 엔티티 객체 모델 기반으로 쿼리 작성
- JPA 또는 하이버네이트가 해당 쿼리를 SQL로 변환해서 실행함
- https://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/Hibernate_User_Guide.html#hql

### JPQL 예시
> Post는 테이블 이름이 아니라 Entity 이름  
> JPA 2.0 부터는 Type을 지정할 수 있고 지정한 Type의 리스트로 출력이 됨  
> 이전에는 Object Type으로 나와서 다 변환해줘야됐었음  

#### Post에 title toString 추가
```java
@Override
public String toString() {
    return "Post{" +
            "title='" + title + '\'' +
            '}';
}
```

#### JpaRunner에서 테스트 로직 구현
```java
TypedQuery<Post> query = entityManager.​createQuery​("SELECT p FROM Post As p", Post.class);
List<Post> posts = query.getResultList();
posts.forEach(System.out::println);
```

## Criteria
https://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/Hibernate_User_Guide.html#criteria
#### 타입 세이프 쿼리
```java
CriteriaBuilder builder = entityManager.​getCriteriaBuilder​();
CriteriaQuery<Post> criteria = builder.createQuery(Post.class);
Root<Post> root = criteria.from(Post.class);
criteria.select(root);
List<Post> posts = entityManager.​createQuery​(criteria).getResultList();
```

## Native Query
https://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/Hibernate_User_Guide.html#sql
> Typed 메서드가 아니더라도 지정한 Type으로 결과값을 리턴해줌  
#### SQL 쿼리 실행하기
```java
List<Post> posts = entityManager
                .createNativeQuery("SELECT * FROM Post", Post.class)
                .getResultList();
```