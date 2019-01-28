# 스프링 데이터 JPA: JPA Repository
> @EnableJpaRepositories 애노테이션을 사용해야 JpaRepository 인터페이스를 상속받은  
> Repository 인터페이스 타입의 Proxy 빈들을 등록 해준다  
## @EnableJpaRepositories
> 스프링 부트 사용할 때는 사용하지 않아도 자동 설정 됨  
> 스프링 부트 사용하지 않을 때는 @Configuration과 같이 사용  

### 설정 옵션
- EntityManager
- transactionManager
- basePackages: package scan을 시작할 base package  
  기본적으로는 @EnableJpaRepositories 애노테이션을 사용한 위치부터 찾기 시작함  
  best Practice는 항상 기본 Package에 위치  
 
## @Repository 애노테이션
> 안붙여도 된다 이미 붙어 있다 또 붙인다고 별일이 생기는건 아니지만 중복일 뿐  
 
## 스프링 @Repository
> SQLExcpetion 또는 JPA 관련 예외를 스프링의 DataAccessException 계층구조 하위클래스중 하나로 변환 해준다  
> 예외만 보더라도 어떤일이 발생했는지 이해하기 쉽도록 변환해줌  
> 기본적인 스프링 프레임워크의 기능  

### DataAccessException의 목적
> SQLExcpetion이 모든 데이터 엑세스 관련된 에러사항을 SQLException 하나로 던져주고  
> CODE 값을 확인해서 무슨 에러인지 찾아봐야 되는 불편함이 있었음  
> 그래서 DataAccessException 계층구조를 만들고 SQLExcpetion에 들어있는 CODE 값에 따라서  
> 굉장히 구체적인 하위 클래스들 중하나로 Mapping을 해서 클래스 이름만 봐도 알 수 있음  
> Hibernate, JPA 같은 경우는 이미 충분히 예외가 잘 만들어져 있음  
> JpaSystemException은 DataAccessException으로 변환 된것 임  