package me.freelife.springjpaprograming;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    @Test
    @Rollback(false)
    public void crudRepository() {
        // Given - 이런 조건 하에서
        Post post = new Post();
        post.setTitle("hello spring boot common");
        assertThat(post.getId()).isNull();

        // When - 이렇게 했을때
        Post newPost = postRepository.save(post);

        // Then - 이렇게 되길 바란다
        assertThat(newPost.getId()).isNotNull();

        // when
        List<Post> posts = postRepository.findAll();

        // Then
        assertThat(posts.size()).isEqualTo(1);
        assertThat(posts).contains(newPost); // newPost 인스턴스를 posts 컬렉션이 가지고 있어야 된다

        // When - 0페이지 부터 10개의 페이지를 달라고 요청
        Page<Post> page = postRepository.findAll(PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(1); // 전체 페이지 개수
        assertThat(page.getNumber()).isEqualTo(0); // 현재 페이지 넘버
        assertThat(page.getSize()).isEqualTo(10); // 요청했던 사이즈
        assertThat(page.getNumberOfElements()).isEqualTo(1); // 현재 페이지에 들어올 수있는 개수

        page = postRepository.findByTitleContains("spring", PageRequest.of(0, 10));

        assertThat(page.getTotalElements()).isEqualTo(1); // 전체 페이지 개수
        assertThat(page.getNumber()).isEqualTo(0); // 현재 페이지 넘버
        assertThat(page.getSize()).isEqualTo(10); // 요청했던 사이즈
        assertThat(page.getNumberOfElements()).isEqualTo(1); // 현재 페이지에 들어올 수있는 개수

        // when - spring을 가지고 있는 개수를 모두 센다
        long spring = postRepository.countByTitleContains("spring");
        // then
        assertThat(spring).isEqualTo(1); // 개수는 1이다
    }
}
