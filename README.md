# 핵심 개념 이해 정리
- 데이터베이스와 자바
- 패러다임 불일치
- ORM이란?
- JPA 사용법 (엔티티, 벨류 타입, 관계 맵핑)
- JPA 특징 (엔티티 상태 변화, Cascade, Fetch, 1차 캐시, ...)

## 주의할 점
- 반드시 발생하는 SQL을 확인할 것.
- 팁: “?”에 들어있는 값 출력하기
  > application.properties에 아래의 옵션을 주면 SQL 파라메터 값까지 볼 수 있다  
  - `logging.level.org.hibernate.SQL=debug`  
    > spring.jpa.show-sql=true 를 주면 안줘도 됨  
  - `logging.level.org.hibernate.type.descriptor.sql=trace`