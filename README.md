# JPA 프로그래밍 3. Value 타입 맵핑

## 엔티티 타입과 Value 타입 구분
- 식별자가 있어야 하는가
- 독립적으로 존재해야 하는가

## Value 타입 종류
> 다른 타입에 종속적인 타입을 Value 타입이라고 보면 됨  
- 기본 타입 (String, Date, Boolean, ...)
- Composite Value 타입
- Collection Value 타입
  - 기본 타입의 콜렉션
  - 컴포짓 타입의 콜렉션

## Composite Value 타입 맵핑
- @Embadable: Composite Value 클래스에 지정하면 해당 클래스를 Composite Value로 만듬
- @Embadded: Entity에서 Composite Value로 지정한 클래스를 불러와 정의할 때 사용
- @AttributeOverrides: 여러 값을 오버라이딩 하기위한 그룹 어노테이션
- @AttributeOverride: 오버라이딩 하기 위해 사용

```java
@Embeddable
public class Address {
    private String street;
    private String city;
    private String state;
    private String zipCode;
}
```
  
```java
// Account
@Embedded
@AttributeOverrides({
    @AttributeOverride(name = "street", column = @Column(name = "home_street"))
})
private Address address;
```