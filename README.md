# 스프링 데이터 JPA 소개 및 원리

## JpaRepository<Entity, Id> 인터페이스
- 매직 인터페이스
- @Repository가 없어도 빈으로 등록해 줌

## @EnableJpaRepositories
- 매직의 시작은 여기서 부터

## 매직은 어떻게 이뤄지나?
> JpaRepository 로 구현한 Repository가 어떻게 자동으로 빈으로 등록됐는지는 아래의 클래스에서 확인  
- 시작은 @Import(​JpaRepositoriesRegistrar.class​)
- 핵심은 ​ImportBeanDefinitionRegistrar​ 인터페이스
  - SpringFrameWork의 인터페이스이며 구현체가 다양함
  - 빈을 프로그래밍을 통해서 등록할 수 있게 해줌
  - JpaRepository를 상속받은 모든 인터페이스들을 찾아서 빈으로 등록해줌

### ​ImportBeanDefinitionRegistrar​ 예제
> Freelife를 빈으로 등록하는 예제  

#### Freelife 클래스 구현
```java
public class Freelife {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

#### FreelifeRegistrar 클래스 구현
> 빈으로 등록하는 프로그래밍 과정  
```java
public class FreelifeRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(Freelife.class);
        beanDefinition.getPropertyValues().add("name", "Superman");

        registry.registerBeanDefinition("freelife", beanDefinition);
    }
}
```

#### @Import 설정
> 최종적으로 아래와 같이 설정하면 프로그래밍에 의해서 자동으로 빈으로 등록됨  
```java
@SpringBootApplication
@Import(FreelifeRegistrar.class)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```

#### 등록된 빈을 주입받아서 출력 테스트
```java
@Component
@Transactional
public class JpaRunner implements ApplicationRunner {

    @Autowired
    Freelife freelife;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        System.out.println("=====================");
        System.out.println(freelife.getName());
    }
}
```

### 예전 방식
> 코드도 작성하고 테스트도 해야되고 매우 번거로움  
```java
@Repository
@Transactional
public class PostRepository {

    @PersistenceContext
    EntityManager entityManager;

    public Post add(Post post) {
        entityManager.persist(post);
        return post;
    }

    public void delete(Post post) {
        entityManager.remove(post);
    }

    public List<Post> findAll() {
        return entityManager.createQuery("SELECT p FROM Post AS p", Post.class)
                .getResultList();
    }
}
```

#### 예전 정의 방식
> 예전에는 기본적인 코드들은 만들어서 정의해서 사용하는 프레임워크가 유행했었음
```java
@Repository
public class PostRepository extends GenericRepository<Post, Long> {

}
```

### 현재의 방식
> PostRepository 라는 interface를 만들고 JpaRepository라는 interface를 상속받음  
> JpaRepository 첫번째 타입은 Entity 타입이고 두번째 타입은 Entity에서 사용하는 PK의 Type  
> @EnableJpaRepositories 는 스프링부트가 자동 설정해줌  
> 아래와 같이 구현하면 @Repository를 지정할 필요없이 빈으로 등록됨  
#### PostRepository 인터페이스 구현
```java
public interface PostRepository extends JpaRepository<Post, Long> {
}
```

#### JpaRunner 테스트 구현
> EntityManager로 복잡하게 구현했던 Code를 SpringDataJPA를 통해 안정적으로 검증된 Code를 사용하여 간결하게 구현가능  
> 생산성, 유지보수성, 코드의 간결함, 간결한 코드로 인해 테스트 작성이 불필요함  
```java
@Component
@Transactional
public class JpaRunner implements ApplicationRunner {

    @Autowired
    PostRepository postRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        postRepository.findAll().forEach(System.out::println);
    }
}
```