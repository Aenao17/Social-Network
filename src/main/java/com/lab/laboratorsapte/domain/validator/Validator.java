package com.lab.laboratorsapte.domain.validator;

public interface Validator<T>{
    void validate(T entity) throws ValidationException;
}
