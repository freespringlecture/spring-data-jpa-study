package me.freelife.springjpaprograming.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostController {

    @Autowired
    private PostRepository postRepository;

    //생성자가 하나만 있는 경우 자동으로 빈으로 주입을 해줌
    /*
    public PostController(PostRepository postRepository) {
        this.postRepository = postRepository;
    }
    */

    @GetMapping("/posts/{id}")
    public String getPost(@PathVariable("id") Post post) {
        return post.getTitle();
    }
}
