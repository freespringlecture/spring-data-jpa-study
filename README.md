# JPA 프로그래밍 2. 엔티티 타입 맵핑
> 맵핑하는 방법은 두가지가 있음 XML 방식, Annotation 방식  
> 하지만 대부분이 Annotation 방식으로 맵핑함  
> 소스코드와 가깝고 가시적인 효과 때문에 그런 것 같음  

## @Entity
- **엔티티**는 객체 세상에서 부르는 이름
- 기본적으로 `@Table` 을 포함하고 있음
- 보통 클래스와 같은 이름을 사용하기 때문에 값을 변경하지 않음  
  > 아래와 같은 형식으로 이름을 클래스와 다르게 줄 수 있음  
  
```java
@Entity(name = "myAccount")
``` 
- 엔티티의 이름은 JPQL에서 쓰임  
  
## @Table
- **릴레이션** 세상에서 부르는 이름
- @Entity의 이름이 기본값  
  > 아래와 같은 형식으로 Table명을 다르게 줄 수 있음  
```java
@Table(name = "myTable")
``` 
- 테이블의 이름은 SQL에서 쓰임  
  
## @Id
- 엔티티의 주키를 맵핑할 때 사용
- 자바의 모든 primitive 타입과 그 랩퍼 타입을 사용할 수 있음
  - Date랑 BigDecimal, BigInteger도 사용 가능
- 복합키를 만드는 맵핑하는 방법도 있지만 그건 논외로..

## @GeneratedValue
- 주키의 생성 방법을 맵핑하는 애노테이션
- 생성 전략과 생성기를 설정할 수 있다  
  > 다른 전략을 임의로 지정할 수 있음 보통은 기본값을 사용  
```java
@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = ...)
```
  - 기본 전략은 AUTO: 사용하는 DB에 따라 적절한 전략 선택
  - TABLE, SEQUENCE, IDENTITY 중 하나

## @Column
- unique: unique 설정여부 true, false를 줄 수 있음
- nullable: null 사용여부 true, false를 줄 수 있음
- length
- columnDefinition: 반드시 SQL을 사용해 명시해주어야 할때 설정
- ...

## @Temporal
> JPA 2.2 부터는 LocalDate, LocalDateTime 나 Java8의 새로운 Date 객체드를 기본으로 지원하기 때문에 애노테이션을 지정하지 않아도됨  
- 현재 JPA 2.1까지는 Date와 Calendar만 지원

## @Transient
> 컬럼으로 맵핑하고 싶지 않은 멤버 변수에 사용  

## application.properries
- JPA에서 자동으로 생성해서 처리한 SQL을 Console에서 볼 수 있음
- 현재는 맵핑되는 값은 보이지 않는데 특별한 Logger설정에 의해 값도 보이게 할 수 있다
```
spring.jpa.show-sql=true
```

- 위에서 보여주는 SQL좀 더 보기 좋게 Console에 출력해줌
```
spring.jpa.properties.hibernate.format_sql=true
```