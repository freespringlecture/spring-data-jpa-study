package me.freelife.springjpaprograming.post;

import org.springframework.context.ApplicationEvent;

public class PostPublishedEvent extends ApplicationEvent {

    private final Post post;
    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public PostPublishedEvent(Object source) {
        super(source);
        this.post = (Post) source;
    }

    public Post getPost() {
        return post;
    }
}
