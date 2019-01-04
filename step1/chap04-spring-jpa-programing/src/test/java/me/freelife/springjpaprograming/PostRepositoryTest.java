package me.freelife.springjpaprograming;

import me.freelife.springjpaprograming.post.Post;
import me.freelife.springjpaprograming.post.PostPublishedEvent;
import me.freelife.springjpaprograming.post.PostRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
@Import(PostRepositoryTestConfig.class)
public class PostRepositoryTest {

    @Autowired
    PostRepository postRepository;

    /*
    @Autowired
    ApplicationContext applicationContext;

    @Test
    public void event() {
        Post post = new Post();
        post.setTitle("event");
        PostPublishedEvent event = new PostPublishedEvent(post);

        applicationContext.publishEvent(event);
    }
    */

    @Test
    public void crud() {
        Post post = new Post();
        post.setTitle("hibernate");

        // save 하기 전에 postRepository.contains안에 post가 들어있는지 검증
        // post가 들어있지 않은 Transient 상태
        assertThat(postRepository.contains(post)).isFalse();

        postRepository.save(post.publish());

        // post가 들어있는 Persist 상태
        assertThat(postRepository.contains(post)).isTrue();

        postRepository.delete(post);
        postRepository.flush();
    }
}
