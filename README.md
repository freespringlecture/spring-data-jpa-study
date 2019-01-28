# JPA 프로그래밍 4. 관계 맵핑

## 관계에는 항상 두 엔티티가 존재 합니다.
- 둘 중 하나는 그 관계의 주인(owning)이고
- 다른 쪽은 종속된(non-owning) 쪽입니다.
- 해당 관계의 반대쪽 레퍼런스를 가지고 있는 쪽이 주인.

## 단방향에서의 관계의 주인은 명확하다
- 관계를 정의한 쪽이 그 관계의 주인입니다.단방향 @ManyToOne
- 기본값은 FK 생성

## 단방향 @OneToMany
- 기본값은 조인 테이블 생성

## 양방향
- FK 가지고 있는 쪽이 오너 따라서 기본값은 @ManyToOne 가지고 있는 쪽이 주인
- 주인이 아닌쪽(@OneToMany쪽)에서 mappedBy 사용해서 관계를 맺고 있는 필드를 설정해야 함

## 양방향
- @ManyToOne (관계를 정의하는 이쪽이 주인)
  > 자기자신 안에 적용한 Entity에 대한 Foerign key를 생성함
- @OneToMany(mappedBy)
- 주인한테 관계를 설정해야 DB에 반영이 됩니다.

## 단방향 관계 예제 실습
### @ManyToOne 예시 
> 스터디와 관계 맵핑시 어떤 스터디를 만드는 사람이 여러개의 스터디를 만들 수 있음  
> 그러면 스터디 입장에서는 @ManyToOne이 됨  
> 해당 클래스안에 레퍼런스가 1이면 @ManyToOne 이다  

#### Study 클래스 작성
```java
@Entity
public class Study {

    @Id @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne
    private Account owner;
}
```

#### Study @ManyToOne 테스트 로직
> Study 테이블안에 Account 테이블의 PK를 참조하는 FK 컬럼을 생성해서 가지고 있게 됨  
> owner라고 줬지만 owner_id라고 생성이 되고 owner_id에 대한 constraints 가 foreign key로 잡힘  
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

        Study study = new Study();
        study.setName("Spring Data JPA");
        study.setOwner(account);

        entityManager.persist(account);
        entityManager.persist(study);
    }
}
```

### @OneToMany 예시
#### Account 클래스에 studies 선언하고 @OneToMany 적용
> 일대다 관계  
```java
@Entity
public class Account {

    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;
    private String password;

    @OneToMany
    private Set<Study> studies = new HashSet<>();
}
```

#### Account @OneToMany 테스트 로직
> account_studies 라는 Mapping 테이블을 만듬  
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

        Study study = new Study();
        study.setName("Spring Data JPA");

        account.getStudies().add(study);

        entityManager.persist(account);
        entityManager.persist(study);
    }
}
```

## 양방향 관계 예제 실습
> 서로가 서로를 참조하도록 만드려면 양방향 관계를 만들어야 함  

#### @ManyToOne 로 Study 설정
```java
@Entity
public class Study {

    @Id @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne
    private Account owner;
}
```

#### @OneToMany 쪽에 양방향 설정
- 관계를 정의한 필드를 @OneToMany(mappedBy = "owner") 이렇게 설정해줘야함
- Study의 Account가 @ManyToOne로 지정되어있어서 Owner이라는 것을 Account studiesdp mappedBy로 지정해서 알려줘야함
- Account가 Study에 종속된 관계로 적용됨 별도의 맵핑 테이블을 생성하지 않음
- 양방향 시에는 Foreign Key를 가진쪽이 Owner 임
- 가장 기본적인 양방향 맵핑방법
```java
@Entity
public class Account {

    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false, unique = true)
    private String username;
    private String password;

    @OneToMany(mappedBy = "owner")
    private Set<Study> studies = new HashSet<>();
}
```

#### 양방향 관계 테스트 로직
> 반드시 주인이 되는 쪽에 관계를 설정하는 로직을 해야함  
> 주인이 되는 쪽만 설정해줘도 되지만 객체지향적으로 서로서로 관계를 반드시 설정해줘야함  
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

        Study study = new Study();
        study.setName("Spring Data JPA");

        account.getStudies().add(study); // 종속관계 관계 설정
        study.setOwner(account); //주인이 되는 쪽 관계 설정

        entityManager.persist(account);
        entityManager.persist(study);
    }
}
```

## 양방향 관계 설정 한묶음으로 처리 
> 객체지향적으로 생각하면 서로간의 레퍼런스를 서로가 가지고 있어야함  
> 양방향 관계이면 관계설정 메서드가 반드시 한 묶음으로 다녀야함  

### 각각 관계설정하는 것이 아닌 한묶음으로 처리
> 관계를 관리하는 메서드를 컨비니언스(convinience) 메서드 라고 함  
#### 기존 로직
```java
account.getStudies().add(study);
study.setOwner(account);
```
#### 한 묶음으로 처리하는 로직
```java
account.addStudy(study);
```

#### addStudy 메서드는 Account에 정의
```java
public void addStudy(Study study) {
    this.getStudies().add(study);
    study.setOwner(this);
}
```

#### remove할때 객체와 관계를 비워주는 메서드 정의
```java
public void removeStudy(Study study) {
    this.getStudies().remove(study); // 스터디 제거
    study.setOwner(null); // 참조하고 있던 Owner가 더이상 owner가 아니도록 설정
}
```