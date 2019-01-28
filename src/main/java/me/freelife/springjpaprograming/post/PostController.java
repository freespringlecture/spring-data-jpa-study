package me.freelife.springjpaprograming.post;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PostController {

    @Autowired
    PostRepository posts;

    @GetMapping("/posts/{id}")
    public String getPosts(@PathVariable("id") Post post) {
        return post.getTitle();
    }

}
