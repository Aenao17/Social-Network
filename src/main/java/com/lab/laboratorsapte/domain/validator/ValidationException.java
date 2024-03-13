package com.lab.laboratorsapte.domain.validator;

public class ValidationException extends RuntimeException{
    ValidationException(String msg){
        super(msg);
    }
}
