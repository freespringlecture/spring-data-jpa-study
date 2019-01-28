# 스프링 데이터 Common: Web 4부: HATEOAS
> HATEOAS를 사용하면 정보들을 유추해서 만들 필요 없이 바로 링크의 이름만 알면 뭘 뜻하는지 이해가 된다면 URL을 바로 가져다가 사용할 수있음  
> 이것이 HATEOAS의 장점이고 이런 HATEOAS를 지원하는 기능이 스프링 데이터 Common에서 제공이되고 있음  
> 이 기능을 사용하려면 WebMVC가 들어있어야하고 HATEOAS 도 들어있어야 사용이 가능  

## Page를 PagedResource로 변환하기 실습
#### HATEOAS 의존성 추가 (starter-hateoas)
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-hateoas</artifactId>
</dependency>
```

#### 핸들러 매개변수로 PagedResourcesAssembler 추가
> PagedResourcesAssembler의 타입을 Entity로 주면 해당 타입을 Resource로 변환해줌  
> 그리고 리턴타입을 PagedResourcesAssembler가 만들어준 PagedResources로 한번 더 감싸줌  
> `assembler.toResource()` 는 Page를 받으므로 `post.findAll(pageable)` 를 그대로 넣어주면 됨  
```java
@GetMapping("/posts")
public PagedResources<Resource<Post>> getPosts(Pageable pageable, PagedResourcesAssembler<Post> assembler) {
    return assembler.toResource(posts.findAll(pageable));
}
```

#### 테스트 코드
```java
@Test
public void getPosts() throws Exception {

    createPosts();

    mockMvc.perform(get("/posts/")
                .param("page", "3")
                .param("size", "10")
                .param("sort", "created,desc")
                .param("sort", "title"))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content[0].title", is("jpa")));
}

private void createPosts() {
    int postsCount = 100;
    while (postsCount > 0) {
        Post post = new Post();
        post.setTitle("jpa");
        postRepository.save(post);
        postsCount--;
    }
}
```
 
### 리소스로 변환하기 전
> HATEOAS가 없이 리턴한 정보는 이 정보들을 유추해서 페이징 및 화면단을 개발해야 함  
```json
{  
   "content":[  
...
      {  
         "id":111,
         "title":"jpa",
         "created":null
      }
   ],
   "pageable":{  
      "sort":{  
         "sorted":true,
         "unsorted":false
      },
      "offset":20,
      "pageSize":10,
      "pageNumber":2,
      "unpaged":false,
      "paged":true
   },
   "totalElements":200,
   "totalPages":20,
   "last":false,
   "size":10,
   "number":2,
   "first":false,
   "numberOfElements":10,
   "sort":{  
      "sorted":true,
      "unsorted":false
   }
}
```

### 리소스로 변환한 뒤
> 페이지와 관련된 리소스정보들(HyperMedia 정보)이 링크로 들어오고 페이지 부가정보도 들어옴 이것이 HATEOAS의 핵심  
```json
{  
   "_embedded":{  
      "postList":[  
         {  
            "id":140,
            "title":"jpa",
            "created":null
         },
...
         {  
            "id":109,
            "title":"jpa",
            "created":null
         }
      ]
   },
   "_links":{  
      "first":{  
         "href":"http://localhost/posts?page=0&size=10&sort=created,desc&sort=title,asc"
      },
      "prev":{  
         "href":"http://localhost/posts?page=1&size=10&sort=created,desc&sort=title,asc"
      },
      "self":{  
         "href":"http://localhost/posts?page=2&size=10&sort=created,desc&sort=title,asc"
      },
      "next":{  
         "href":"http://localhost/posts?page=3&size=10&sort=created,desc&sort=title,asc"
      },
      "last":{  
         "href":"http://localhost/posts?page=19&size=10&sort=created,desc&sort=title,asc"
      }
   },
   "page":{  
      "size":10,
      "totalElements":200,
      "totalPages":20,
      "number":2
   }
}
```