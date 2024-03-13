package com.lab.laboratorsapte.repository;

import com.lab.laboratorsapte.domain.Entity;
import com.lab.laboratorsapte.domain.validator.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepository <ID, E extends Entity<ID>> implements Repository<ID,E> {

    protected Map<ID,E> entities;
    protected Validator validator;

    public InMemoryRepository(Validator val){
        this.validator = val;
        entities = new HashMap<ID,E>();
    }

    @Override
    public Optional<E> findOne(ID id)
    {
        if(id==null)
            throw new IllegalArgumentException("Id nu poate sa fie null!");
        return Optional.ofNullable(entities.get(id));
    }

    @Override
    public Iterable<E> findAll()
    {
        return entities.values();
    }

    @Override
    public Optional<E> save(E entity)
    {
        if (entity == null) {
            throw new IllegalArgumentException("Id nu poate sa fie null!");
        }
        this.validator.validate(entity);
        return Optional.ofNullable(entities.putIfAbsent(entity.getId(),entity));
    }

    @Override
    public Optional<E> delete(ID id)
    {
        if(id==null)
            throw new IllegalArgumentException("Id nu poate sa fie null!");
        return Optional.ofNullable(entities.remove(id));
    }

    @Override
    public Optional<E> update(E entity)
    {
        if (entity == null) {
            throw new IllegalArgumentException("Id nu poate sa fie null!");
        }
        entities.put(entity.getId(), entity);
        if(entities.get(entity.getId())!=null)
        {
            entities.put(entity.getId(), entity);
            return Optional.empty();
        }
        return Optional.of(entity);
    }
}
