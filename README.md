# 스프링 데이터 Common: Web 1부: 웹 지원 기능 소개
> 스프링 데이터를 웹과 같이 사용한다면 추가적인 기능들을 더 제공함  

## 스프링 데이터 웹 지원 기능 설정
- 스프링 부트를 사용하는 경우에.. 설정할 것이 없음 (자동 설정)
- 스프링 부트 사용하지 않는 경우?
```java
@Configuration
@EnableWebMvc
@EnableSpringDataWebSupport
class WebConfiguration {}
```

## 제공하는 기능
> 주로 도메인 클래스 컨버터, Pageable과 Sort 매개변수 사용을 주로 사용할 것 같고  
> REST API를 정말 잘만들고 싶다면 HATEOS 까지 적용을 해야하는데 그런경우에는 Page 관련 HATEOS 기능까지 적용할 수 있을 것 같음  

- 도메인 클래스 컨버터  
  > @PathVariable 혹은 RequestParameter로 들어오는 id 값을 도메인으로 변환을 받아서 파라메터로 받을 수 있음  
- @RequestHandler 메소드에서 Pageable과 Sort 매개변수 사용  
  > 요청이 들어온 특정 파라메터들을 Pageable 또는 Sort로 맵핑해서 바인딩을 해줌  
- Page 관련 HATEOAS 기능 제공
  - PagedResourcesAssembler
  - PagedResoure
  
- Payload 프로젝션
  - 요청으로 들어오는 데이터 중 일부만 바인딩 받아오기
  - @ProjectedPayload, @XBRead, @JsonPath
- 요청 쿼리 매개변수를 QueryDSLdml Predicate로 받아오기
  - ?firstname=Mr&lastname=White => Predicate


## HATEOAS 란?
> 리소스에 대한 HyperMedia를 리소스 정보와 같이 사용하라는 논문의 이야기이며  
> 그 구현체가 Spring HATEOAS  
> 어떠한 리소스를 요청했을 때 리소스와 관련이 있는 링크정보들을 같이 보내는 것을 HATEOAS 라고 할 수 있음  
> 링크 정보는 이름이 있고 URL이 있음  

### Spring HATEOAS 
> Pageable을 파라메터로 받아서 사용하면 Page 타입을 받아서 PagedResources로 변환을 해주는   
> `PagedResourcesAssembler`를 사용할 수 있음  
> `PagedResourcesAssembler`를 사용해서 PagedResources로 변환을 하면  
> Page 정보 뿐만 아니라 HETOAS 라고 불릴만한 Link 정보도 같이 응답으로 만들어 쉽게 보낼 수 있음  

```java
@GetMapping("/posts")
public PagedResources<Post> getPosts(Pageable pageable, PagedResourcesAssembler assembler) {
  Page<Post> all = postRepository.findAll(pageable);
  return assembler.toResoure(all)
}
```

## @ProjectedPayload
https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#core.web.basic 
- HTTP payload binding using JSONPath or XPath expressions  
- 요청으로 들어오는 JSON 또는 XML 데이터의 일부만 캡쳐를 해서 바인딩 받을 수 있는 기능  
- JSON에 여러가지 정보들이 들어오는데 그중에서 원하는 것만 받고 싶다면  
- @ProjectedPayload 애노테이션을 붙이고 @JsonPath, @XBRead를 사용해서 요청안에 들어있는 데이터 일부를 바인딩 받아 올 수 있음  
- 데이터를 보내고 UserPayload 인터페이스 타입으로 바인딩을 받으면 정의한 원하는 데이터를 받아올 수 있음  
- 바인딩을 일부만 받는 경우가 흔치 않고 ModelAttribute를 사용하거나 RequestBody만 사용해도 받을 수 있음
```java
@ProjectedPayload
public interface UserPayload {

  @XBRead("//firstname")
  @JsonPath("$..firstname")
  String getFirstname();

  @XBRead("/lastname")
  @JsonPath({ "$.lastname", "$.user.lastname" })
  String getLastname();
}
```

## QueryDSL Web Support
> 웹에 QueryString이 들어오면 Handler의 매개변수로 QueryString을 QueryDSL이 제공하는 Predicate 타입으로 받아줌  
> 사용하기에 위험해 보여서 권장하지는 않음 뭐가 들어올지도 모르는데 무작정 받아서 Query 하는 것은 너무 위험해 보임  
```java
?firstname=Dave&lastname=Matthews
```

```java
QUser.user.firstname.eq("Dave").and(QUser.user.lastname.eq("Matthews"))
```
  
> Pageable 도 받을 수 있고 Predicate 도 받을 수 있으므로 바로 QueryDSL을 지원하는 Repository를 사용해서  
> 바로 Predicate 와 Pageable 를 전달 받아서 users 를 조회해서 바로 리턴하는 것이 가능  
```java
@Controller
class UserController {

  @Autowired UserRepository repository;

  @RequestMapping(value = "/", method = RequestMethod.GET)
  String index(Model model, @QuerydslPredicate(root = User.class) Predicate predicate,    
          Pageable pageable, @RequestParam MultiValueMap<String, String> parameters) {

    model.addAttribute("users", repository.findAll(predicate, pageable));

    return "index";
  }
}
```