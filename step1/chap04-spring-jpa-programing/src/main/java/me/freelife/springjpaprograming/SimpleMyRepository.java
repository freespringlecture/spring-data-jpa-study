package me.freelife.springjpaprograming;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

public class SimpleMyRepository<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements MyRepository<T, ID> {

    private EntityManager entityManager;

    public SimpleMyRepository(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }


    //entity를 전달 받아 PersistentContext 안에 들어 있는지 검증하는 로직 구현
    @Override
    public boolean contains(T entity) {
        return entityManager.contains(entity);
    }

    @Override
    public List<T> findAll() {
        return super.findAll();
    }
}
