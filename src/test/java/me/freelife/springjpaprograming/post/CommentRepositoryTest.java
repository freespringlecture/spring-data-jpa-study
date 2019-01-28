package me.freelife.springjpaprograming.post;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.junit4.SpringRunner;

import static me.freelife.springdatajpautilizestudy.post.CommentSpecs.isBest;
import static me.freelife.springdatajpautilizestudy.post.CommentSpecs.isGood;

@RunWith(SpringRunner.class)
//@DataJpaTest
@SpringBootTest
public class CommentRepositoryTest {

    @Autowired
    CommentRepository comments;

    @Autowired
    PostRepository posts;

    @Test
    public void getComment() {
//        comments.findByPost_Id(1l);
        Post post = new Post();
        post.setTitle("jpa");
        Post savedPost = posts.save(post);

        Comment comment = new Comment();
        comment.setComment("spring data jpa projection");
        comment.setPost(savedPost);
        comment.setUp(10);
        comment.setDown(1);
        comments.save(comment);

        comments.findByPost_Id(savedPost.getId(), CommentOnly.class).forEach(c -> {
            System.out.println("========================");
//            System.out.println(c.getVotes());
            System.out.println(c.getComment());
        });
    }

    @Test
    public void specs() {
//        comments.findAll(CommentSpecs.isBest().or(CommentSpecs.isGood()));
        // static import를 사용해서 코드를 간결하게 함
//        comments.findAll(isBest().or(isGood()));
        //Page로도 받을 수 있음
        Page<Comment> page = comments.findAll(isBest().or(isGood()), PageRequest.of(0, 10));
    }

    @Test
    public void qbe() {
        Comment prove = new Comment();
        prove.setBest(true);

        ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny().withIgnorePaths("up","down");
        Example<Comment> example = Example.of(prove, exampleMatcher);
        comments.findAll(example);
    }
}