package me.freelife.springjpaprograming.post;

import me.freelife.springjpaprograming.post.PostListener;
import me.freelife.springjpaprograming.post.PostPublishedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PostRepositoryTestConfig {

    /*
    @Bean
    public PostListener postListner() {
        return new PostListener();
    }
    */

    @Bean
    public ApplicationListener<PostPublishedEvent> postListener() {
        return new ApplicationListener<PostPublishedEvent>() {
            @Override
            public void onApplicationEvent(PostPublishedEvent event) {
                System.out.println("-----------------");
                System.out.println(event.getPost() + " is published!");
                System.out.println("-----------------");
            }
        };
    }
}
