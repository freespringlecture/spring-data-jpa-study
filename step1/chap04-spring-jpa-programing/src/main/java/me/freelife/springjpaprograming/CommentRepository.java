package me.freelife.springjpaprograming;

import org.springframework.data.domain.Pageable;

import java.util.stream.Stream;

public interface CommentRepository extends MyRepository<Comment, Long>{

    //Comment 조회
    //List<Comment> findByCommentContains(String keyword);
    //IgnoreCase 추가
    //List<Comment> findByCommentContainsIgnoreCase(String keyword);
    //likeCount가 특정 숫자보다 높을 때 조건 추가
    //List<Comment> findByCommentContainsIgnoreCaseAndLikeCountGreaterThan(String keyword, int likeCount);
    //Comment를 likeCount 높은 순으로 정렬
    //List<Comment> findByCommentContainsIgnoreCaseOrderByLikeCountDesc(String keyword);
    //Pageable로 동적으로 정렬
    //Page<Comment> findByCommentContainsIgnoreCase(String keyword, Pageable pageable);
    //Stream으로 받아오기
    Stream<Comment> findByCommentContainsIgnoreCase(String keyword, Pageable pageable);
}
