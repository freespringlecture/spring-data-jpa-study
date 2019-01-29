# JPA 프로그래밍 1. 프로젝트 세팅

## 데이터베이스 실행
- PostgreSQL 도커 컨테이너 재사용
- docker start postgres_boot

## 스프링 부트
- 스프링 부트 v2.*
- 스프링 프레임워크 v5.*

## 스프링 부트 스타터 JPA
- JPA 프로그래밍에 필요한 의존성 추가
  - JPA v2.*
  - Hibernate v5.*
- 자동 설정: `HibernateJpaAutoConfiguration`  
  > JPA에 필요한 모든 빈 들이 자동으로 등록됨  
    
  - `JpaBaseConfiguration` 의 하위 클래스(`EntityManagerFactoryBuilder`, `LocalContainerEntityManagerFactoryBean`)
  - 컨테이너가 관리하는 `EntityManager` (프록시) 빈 설정
  - `PlatformTransactionManager` 빈 설정

## application.properties 설정
#### JDBC 설정
```
spring.datasource.url=jdbc:postgresql://localhost:5432/springdata
spring.datasource.username=freelife
spring.datasource.password=pass
```
#### JPA ddl 자동설정 옵션
> 운영시에는 create와 update는 사용하면 안되고 flyway 같은 migration 툴을 사용해야함
- create: 스키마와 데이터가 매번 새롭게 생성됨 개발시 사용
- update: 스키마와 데이터를 유지하면서 스키마나 데이터에 대한 변경사항을 적용(스키마 변경시 이전 스키마가 남아있음)
- create-drop: 스키마와 데이터가 매번 새롭게 생성되고 종료시 제거 됨
- validate: 스키마를 검증만 해준다 운영시 사용
```
spring.jpa.hibernate.ddl-auto=create
```

#### createClob() 관련된 경고 무시설정
> postgresql 드라이버가 createClob() 메서드를 구현하지 않아 경고문구가 출력됨  
```
spring.jpa.properties.hibernate.jdbc.lob.non_contextual_creation=true
```

### 맵핑 애노테이션
- @Id: 데이터베이스 주키(Primary Key)에 맵핑
- @GeneratedValue: 자동 생성 설정 
- @Entity: 클래스명에 해당되는 테이블에 맵핑

## 프로젝트 셋팅
#### Account Domain Class Create
- Member Variables들은 @Column 애노테이션이 생략된거랑 마찬가지임
- @Entity 애노테이션으로 인해 모두 테이블에 자동 맵핑되어 컬럼이 생성됨
- 애플리케이션을 실행하면 datasource 타입의 빈을 만들고 빈 들은 application.properties 정보를 참조해서 만들어짐
```java
@Entity
public class Account {

    @Id @GeneratedValue
    private Long id;
    private String username;
    private String password;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
```

#### JpaRunner 생성
- @PersistenceContext를 통해서 JPA의 핵심인 EntityManager를 주입 받음
- 이 클래스를 통해서 Entity들을 영속화 할 수 있음(데이터베이스에 저장)
- JPA와 관련된 모든 Operation들은 한 Transaction 안에서 일어나야함
- Spring에서 제공하는 @Transactional을 사용 클래스,메서드에 적용할 수 있음

```java
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
@Transactional
public class JpaRunner implements ApplicationRunner {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Account account = new Account();
        account.setUsername("freelife");
        account.setPassword("pass");

        entityManager.persist(account);
    }
}
```

#### hiebernate로 처리 가능
- JPA가 hibernate를 사용하므로 hibernate도 사용할 수 있음
- Hibernate의 가장 핵심적인 API인 Session을 활용해 저장할 수 있다
```java
@Component
@Transactional
public class JpaRunner implements ApplicationRunner {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Account account = new Account();
        account.setUsername("freelife");
        account.setPassword("hibernate");

        Session session = entityManager.unwrap(Session.class);
        session.save(account);
    }
}

```