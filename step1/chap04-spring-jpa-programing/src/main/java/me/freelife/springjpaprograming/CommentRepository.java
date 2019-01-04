package me.freelife.springjpaprograming;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentRepository extends MyRepository<Comment, Long>{
    //Comment title에 Keyword가 들어있는 모든 Comment를 찾아주는 메서드
    List<Comment> findByCommentContains(String Keyword);
    //어떠한 post에 들어가 있으면서 그중에서 like가 몇개 이상인 Comment를 조회해오는 메서드
    Page<Comment> findByLikeCountGreaterThanAndPost(int likeCount, Post post, Pageable pageable);
}
