package me.freelife.springjpaprograming.post;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long>, JpaSpecificationExecutor<Comment> {

//    @EntityGraph(value = "Comment.post")
    @EntityGraph(attributePaths = "post")
    Optional<Comment> getById(Long id);

//    List<CommentSummary> findByPost_Id(Long id);
    <T> List<T> findByPost_Id(Long id, Class<T> type);


}