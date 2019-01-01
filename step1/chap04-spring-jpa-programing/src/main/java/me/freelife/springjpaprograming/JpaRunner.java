package me.freelife.springjpaprograming;

import org.hibernate.Session;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

@Component
@Transactional
public class JpaRunner implements ApplicationRunner {

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // JPQL Example
        /*
        TypedQuery<Post> query = entityManager.createQuery("SELECT p FROM Post As p", Post.class);
        List<Post> posts = query.getResultList();
        */

        // Criteria
        /*
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Post> criteria = builder.createQuery(Post.class);
        Root<Post> root = criteria.from(Post.class);
        criteria.select(root);

        List<Post> posts = entityManager.createQuery(criteria).getResultList();
        */

        // Native Query
        List<Post> posts = entityManager
                .createNativeQuery("SELECT * FROM post", Post.class)
                .getResultList();

        posts.forEach(System.out::println);
    }
}
