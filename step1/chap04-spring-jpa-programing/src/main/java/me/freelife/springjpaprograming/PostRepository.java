package me.freelife.springjpaprograming;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EnableJpaRepositories
public interface PostRepository extends JpaRepository<Post, Long> {
}
