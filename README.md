# 스프링 데이터 Common: QueryDSL
> 메서드 이름을 분석해서 스프링 데이터 JPA(Common)가 자동으로 쿼리를 만드는 방법은  
> 조건이 추가될 수록 너무 알아보기 힘든 단점이 있음  
```java
findByFirstNameIngoreCaseAndLastNameStartsWithIgnoreCase(String firstName, String lastName) 
```
## 여러 쿼리 메소드는 대부분 두 가지 중 하나.
- Optional<T> findOne(Predicate): 이런 저런 조건으로 무언가 하나를 찾는다.
- List<T>|Page<T>|.. findAll(Predicate): 이런 저런 조건으로 무언가 여러개를 찾는다
- QuerydslPredicateExecutor 인터페이스
 
## QueryDSL(Domain Specific Language)
- http://www.querydsl.com/
- 타입 세이프한 쿼리 만들 수 있게 도와주는 라이브러리
- JPA, SQL, MongoDB, JDO, Lucene, Collection 지원
- QueryDSL JPA 연동 가이드
 
## 스프링 데이터 JPA + QueryDSL
- 인터페이스: QuerydslPredicateExecutor<T>
- 구현체: QuerydslPredicateExecutor<T>
 
## 연동 방법
- 기본 리포지토리 커스터마이징 안 했을 때. (쉬움)
- 기본 리포지토리 커스타마이징 했을 때. (해맬 수 있으나... 제가 있잖습니까)
 
## 의존성 추가
http://www.querydsl.com/static/querydsl/4.0.1/reference/ko-KR/html_single/
  
> 아래와 같이 의존성을 다 추가한 다음 Maven - Lifecycle - compile 클릭해서 빌드  
> 빌드하면 target/generated-sources/java에 클래스가 생성됨  
  
#### QueryDSL 의존성 추가
> QueryDSL은 스프링 부트가 의존성을 관리해주므로 버전을 명시하지 않아도 됨  
> `apt`모듈은 QueryDSL이 Entity모델을 보고 Query용 Specific Language(특정 언어)를 만들어 주는 모듈  
```xml
<dependency>
  <groupId>com.querydsl</groupId>
  <artifactId>querydsl-apt</artifactId>
</dependency>
<dependency>
  <groupId>com.querydsl</groupId>
  <artifactId>querydsl-jpa</artifactId>
</dependency>
```

#### QueryDSL용 빌드 설정
```xml
<plugin>
  <groupId>com.mysema.maven</groupId>
  <artifactId>apt-maven-plugin</artifactId>
  <version>1.1.3</version>
  <executions>
    <execution>
      <goals>
        <goal>process</goal>
      </goals>
      <configuration>
        <outputDirectory>target/generated-sources/java</outputDirectory>
        <processor>com.querydsl.apt.jpa.JPAAnnotationProcessor</processor>
      </configuration>
    </execution>
  </executions>
</plugin>
```

## 실습
#### AccountRepository에 QuerydslPredicateExecutor를 추가 <T>타입에는 도메인
```java
public interface AccountRepository extends JpaRepository<Account, Long>, QuerydslPredicateExecutor<Account> {
}
```

#### 테스트 코드 구현
```java
@RunWith(SpringRunner.class)
@DataJpaTest
public class AccountRepositoryTest {

    @Autowired
    AccountRepository accountRepository;

    @Test
    public void crud(){
        QAccount account = QAccount.account;
        Predicate predicate = account
                .firstName.containsIgnoreCase("freelife")
                .and(account.lastName.startsWith("super"));

        Optional<Account> one = accountRepository.findOne(predicate);
        assertThat(one).isEmpty();
    }
}
```

## 2-9 도메인 이벤트 프로젝트 QueryDSL로 커스터마이징
> Maven 의존성 추가후 clean - compile  

#### PostRepository에 QuerydslPredicateExecutor 추가
```java
public interface PostRepository extends MyRepository<Post, Long>, QuerydslPredicateExecutor<Post> {
}
```

#### 테스트 코드 구현
```java
@RunWith(SpringRunner.class)
@DataJpaTest
@Import(PostRepositoryTestConfig.class)
public class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @Test
    public void crud() {
        Post post = new Post();
        post.setTitle("hibernate");
        postRepository.save(post.publish());

        Predicate predicate = QPost.post.title.containsIgnoreCase("hi");
        Optional<Post> one = postRepository.findOne(predicate);
        assertThat(one).isNotEmpty();
    }
}
```