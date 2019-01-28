package me.freelife.springjpaprograming.post;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class PostControllerTest {

  @Autowired
  private PostRepository postRepository;

  @Test
  public void crud() {
    Post post = new Post();
    post.setTitle("Jpa");
    postRepository.save(post);

    List<Post> all = postRepository.findAll();
    assertThat(all.size()).isEqualTo(1);
  }

}
