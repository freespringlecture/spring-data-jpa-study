package me.freelife.springjpaprograming;

import org.hibernate.Session;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Component
@Transactional
public class JpaRunner implements ApplicationRunner {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // Post(Parent) Comment(Child) Cascade 테스트 예제
        /*
        Post post = new Post();
        post.setTitle("Spring Data JPA 언제 보나...");

        Comment comment = new Comment();
        comment.setComment("빨리 보고 싶어요.");
        post.addComment(comment);

        Comment comment1 = new Comment();
        comment1.setComment("곧 보여드릴께요.");
        post.addComment(comment1);

        Session session = entityManager.unwrap(Session.class);
        session.save(post);
        */
        // EAGER 테스트
        Session session = entityManager.unwrap(Session.class);
        Post post = session.get(Post.class, 4l);
        System.out.println("========================");
        System.out.println(post.getTitle());

        // Lazy n+1 테스트
        post.getComments().forEach(c -> {
            System.out.println("--------------");
            System.out.println(c.getComment());
        });

        // Lazy 테스트
//        Comment comment = session.get(Comment.class, 5l);
//        System.out.println("=====================");
//        System.out.println(comment.getComment());
//        System.out.println(comment.getPost().getTitle());


    }
}
