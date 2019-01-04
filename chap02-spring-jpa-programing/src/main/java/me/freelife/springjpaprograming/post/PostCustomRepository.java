package me.freelife.springjpaprograming.post;

import java.util.List;

public interface PostCustomRepository<T> {

    List<Post> findMyPost();
    void delete(T entity);
}
