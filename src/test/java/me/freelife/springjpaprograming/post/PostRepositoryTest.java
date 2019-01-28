package me.freelife.springjpaprograming.post;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@DataJpaTest
public class PostRepositoryTest {

  @Autowired
  PostRepository postRepository;

  @PersistenceContext
  private EntityManager entityManager;

  @Test
  public void save() {
    Post post = new Post();
    post.setTitle("Jpa");
    Post savedPost = postRepository.save(post);// id가 없어서 EntityManager.persist()

    // entityManager가 post 인스턴스를 캐싱하고 있으므로 true
    assertThat(entityManager.contains(post)).isTrue();
    // entityManager가 savedPost 인스턴스도 가지고 있으므로 true
    assertThat(entityManager.contains(savedPost)).isTrue();
    // 두 인스턴스는 같음
    assertThat(savedPost == post);

    Post postUpdate = new Post();
    postUpdate.setId(post.getId());
    postUpdate.setTitle("hibernate");
    // 리턴 받는 UpdatedPost 가 영속화가 되고 파라메터로 넘긴 postUpdate는 영속화가 되지 않음
    Post updatedPost = postRepository.save(postUpdate);// id가 있으므로 EntityManager.merge()

    // entityManager가 updatedPost 인스턴스를 캐싱하고 있으므로 true
    assertThat(entityManager.contains(updatedPost)).isTrue();
    // entityManager가 postUpdate 인스턴스를 캐싱하고 있지 않으므로 false
    assertThat(entityManager.contains(postUpdate)).isFalse();
    // 두 인스턴스는 서로 다름
    assertThat(updatedPost == postUpdate);

    // 이런식으로 파라메터로 전달한 인스턴스를 사용하면 persist 상태가 아니므로 상태변화를 감지 안함
    // 그래서 그냥 Hibernate 임 updatedPost를 사용해야 제대로 감지함
    postUpdate.setTitle("ironman");

    List<Post> all = postRepository.findAll();
    assertThat(all.size()).isEqualTo(1);
  }
}
