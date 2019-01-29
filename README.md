# 2. ORM 개요
## ORM(Object Relation Mapping)
> ORM은 애플리케이션의 클래스와 SQL 데이터베이스의 테이블 사이의 ​**맵핑 정보를 기술한 메타데이터**​를 사용하여  
> 자바 애플리케이션의 객체를 SQL데이터베이스의 테이블에 **자동으로 (또 깨끗하게) 영속화​** 해주는 기술  
> Hibernate나 JPA 같은 ORM을 사용해서 궁극적으로 코딩을 하려는 방법은 도메인 모델을 사용하는 방식으로 프로그래밍을 하려함  
> Hibernate가 자동생성하는 SQL뿐만 아니라 임의로 JDBC를 사용하는 Query를 직접 사용할 수도 있음  
  
#### JDBC를 직접 사용하는 방식  
```java
try(Connection connection = DriverManager.getConnection(url, username, password)){
    System.out.println("Connection created: "+ connection);
    String sql = "CREATE TABLE ACCOUNT (id int, username varchar(255), password varchar(255));";
    sql = "INSERT INTO ACCOUNT VALUES(1, 'freelife', 'pass');";
    try(PreparedStatement statement = connection.prepareStatement(sql)){
        statement.execute();
    }
}
```

#### 도메인 모델을 사용하는 코드를 Object 라고 함
```java
Account account = new Account(“freelife”, “pass”);
accountRepository.save(account);
```

## JDBC 대신 도메인 모델 사용하려는 이유?
- 객체 지향 프로그래밍의 장점을 활용하기 좋다
- 각종 디자인 패턴
- 코드 재사용
- 비즈니스 로직 구현 및 테스트 편함
  
## 장단점
| 장점                                              | 단점     |
| ------------------------------------------------- | -------- |
| 생산성<br />유지보수성<br />성능<br />밴더 독립성 | 학습비용 |
  
### 장점
- 생산성: 맵핑만 하면 데이터 입 출력이 정말 쉬워짐
- 유지보수성: 코드가 굉장히 간결해지고 코드양이 줄어 유지보수성이 높아짐
- 성능
  - 논쟁의 여지가 있음 SQL단건만 보면 ORM이 더 느릴수있음 
  - Hibernate는 객체와 테이블에 데이터 사이에 캐시가 존재하므로 불필요한 쿼리를 사용하지 않음
  - 하나의 트랜잭션 내에서 여러 요청이 일어나도 정말로 데이터베이스에 반영해야되는 시점에만 반영을함
  - 데이터가 같거나 반영할 필요가 없다면 반영하지 않아서 성능에 장점이 있음
  - 성능 최적화를 위한 여러가지 기능들을 제공함
- 벤더 독립성: Hibernate가 어떠한 데이터베이스에 맞게 SQL을 생성해야 되는지만 알려주면 됨
  - 데이터베이스가 바뀌어도 코드가 변경되지 않음
  - Hibernate가 데이터베이스 sync를 할때 객체를 영속화 할때 발생하는 SQL만 자동으로 바뀜

### 단점
> 학습비용: SQL과 데이터베이스도 잘 알아야하고 Hibernate와 JPA도 아주 잘 알아야 됨  
  
- Hibernate가 어떠한 SQL을 발생시킬지 알아야한다
- 학습하는데 시간이 많이 들고 어려운 프레임워크 중 하나

## 비침투성 논란의 여지 
- 비침투성(transparent): 자기 자신의 코드를 숨기려고 함
- SpringFrameWork, Hibernate, ORM도 비침투적인 철학을 가지고 있다고 짐작함
- 스프링 부트에서 제공하는 EntityManager을 사용해야 하므로 아주 비 침투적이진 않음