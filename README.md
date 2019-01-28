# 스프링 데이터 JPA: 트랜잭션
https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#transactions
> 스프링 데이터 JPA가 제공하는 Repository의 모든 메소드에는 기본적으로 @Transaction이 적용되어 있습니다  
> 스프링 프레임워크가 제공하는 트랜잭션 기능과 거의 99% 같음 스프링 데이터 JPA에 특화되어있다고 생각하기엔 어려운 파트  
 
## 스프링 @Transactional
https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/transaction/annotation/Transactional.html
> 클래스, 인터페이스, 메소드에 사용할 수 있으며, 메소드에 가장 가까운 애노테이션이 우선 순위가 높다  
> 기본적으로 @Transactional(readOnly=true)는 아무런 @Transactional 설정을 하지 않은 메서드에 적용됨  

### @Transactional 옵션
- `timeout`을 설정해줄 수 있음
- @Transactonal이 사용할 `transactionManager`을 설정해줄 수도 있는데
- 기본적으로 `JpaTransactionManager`을 사용하게 될 것임
- `transactionManager` 여러개 설정하지 않는 이상 건드릴 필요가 없는 설정
- 스프링 부트를 사용하면서 하나만 사용해야되는 경우엔 스프링 부트 자동설정한 빈 네임이
- 자동으로 설정된 `transactionManager`로 자동으로 설정됨
- `transactionManager`의 기본 값이 transactionManager
- `transactionManager`로 참조할 빈의 이름을 transactionManager 로 설정해주기 때문에 별다른 설정을 하지 않아도 됨

### readOnly
> 해당 트랜잭션이 readOnly 인지 나타내는 flag 이 값을 주면서 Optimization 성능 최적화를 해줄 수 있는 여지가 생김  
> 가급적이면 데이터를 변경하는 Operation이 없으면 readOnly를 true로 주는 것이 좋음  

### Rollback
- RuntimeException 이나 Error가 발생하면 해당 트랜잭션을 Rollback 시킴
- checked exception이 발생하면 Rollback 하지 않음
- 추가적으로 checked exception이 발생해도 Rollback이 되게 하려면
  > `rollbackFor` , `rollbackForClassName` 에 설정해주면 됨
- RuntimeException 이 발생하면 Rollback을 안하려면
  > `noRollbackFor`, `noRollbackForClassName` 에 설정해주면 됨

### Isolation
- 트랜잭션이 여러개가 동시에 데이터베이스에 접근했을 때 해당 트랜잭션들을 어떻게 처리할 것인지
- 동시에 실행되게 할 것인지 하나 하나씩 차례대로 데이터에 접근하게 할 것인지
- 이런 제어에 대한 설정인데 어떠한 레벨로 주느냐에 따라 데이터에 동시에 접근했을때 발생할 수 있는 현상이 달라짐
- non-repeatable reads(다시 조회했는데 똑같은 값이 안나오는 것), dirty reads(트랜잭션이 아직 쓰지 않은 데이터를 읽는 것),
  phantom reads(값이 있었는데 없어지는 것)
- 그런 현상들이 발생할 여지가 있고 어떤건 다막을 수있는데 다 막을 수록 성능이 안좋아짐
#### DEFAULT
- 기본값은 데이터베이스의 기본값을 따름
- H2는 기본값: Read Committed 
- PostgreSQL 기본값: Read Committed
- 보통 기본값이 Read Committed

#### READ_COMMITTED
- 트랜잭션에서 데이터를 쓰고 있어도 다른 트랜잭션에서 그 데이터를 안 읽어감
- phantom reads(없는 데이터를 읽는 현상)는 발생할 수 있는데 
- dirty reads(트랜잭션이 아직 쓰지 않은 데이터를 읽는 것) 를 방지할 수 있음
- `READ_UNCOMMITTED` 다음으로 성능이 좋음

#### READ_UNCOMMITTED
- 성능이 가장 좋지만 Commit 안될 데이터까지 읽어갈 여지가 생김
- 한 트랜잭션에서 데이터를 넣고 있는데 데이터가 사라질지 안사라질지는 트랜잭션이 끝나봐야 아는건데
- 다른 트랜잭션에서 사라질 데이터까지 읽어갈 여지가 생김

#### REPEATABLE_READ
- `READ_COMMITTED` 다음으로 성능이 좋음
- dirty reads, non-repeatable 를 방지함, phantom reads는 발생할 수 있음

#### SERIALIZABLE
- dirty reads, non-repeatable, phantom reads 다 막지만 성능이 가장 안좋음
- 데이터베이스에 동시접근할 수 있는 트랜잭션이 하나 뿐임

### Propagation
- 트랜잭션으로 시작한 메서드에서 다른 트랜잭션을 가진 메서드를 호출하면
- 첫번째 메서드의 트랜잭션을 이어갈 것인가 두번째 메서드는 새로운 트랜잭션을 만들어서 처리할 것인가
- 이런 것에 대한 설정
- `MANDATORY`, `NESTED`, `NEVER`, `NOT_SUPPORTED`, `REQUIRED`, `REQUIRES_NEW`, `SUPPORTS`의 옵션이 있음

#### NEVER
> 트랜잭션으로 처리하지 않음  
 
## JPA 구현체로 Hibernate를 사용할 때 트랜잭션을 readOnly를 사용하면 좋은 점
- Flush 모드: 언제 데이터베이스에 sync를 할 것인가 기본값은 적절한 타이밍에 되게 되어있음
  보통 commit 할때 데이터를 read 해오기 전에 flush 하게 되어 있음
> Flush 모드를 NEVER로 설정(Flush를 안함)하여, Dirty checking을 하지 않도록 한다  
> 데이터 변경이 일어나지 않을 트랜잭션이기 때문에 Flush를 하지 않아서 데이터 베이스 동기화를 하지 않음  
> 데이터를 많이 가져오는 경우에 Dirty checking 를 꺼주면 성능이 향상됨  

### Custom 메서드에 사용
> Custom 하게 추가한 메서드에도 `@Transactional`을 사용할 수 있음  

### @Service에서 @Transacitonal 설정
> @Service 계층에서 `@Transactional`을 달아서 여러 Repository를 사용하는 코드가  
> 하나의 트랜잭션으로 처리가 되도록 함  
> Repository도 직접 호출해서 사용하는 경우가 있으므로  
> Repository에도 `@Transactional`을 붙여주는 게 좋은 습관임  
```java
@Service
class UserManagementImpl implements UserManagement {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  @Autowired
  public UserManagementImpl(UserRepository userRepository,
    RoleRepository roleRepository) {
    this.userRepository = userRepository;
    this.roleRepository = roleRepository;
  }

  @Transactional
  public void addRoleToAllUsers(String roleName) {

    Role role = roleRepository.findByName(roleName);

    for (User user : userRepository.findAll()) {
      user.addRole(role);
      userRepository.save(user);
    }
}
```