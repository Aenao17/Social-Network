package com.lab.laboratorsapte.domain.validator;

import com.lab.laboratorsapte.domain.User;

public class UserValidator implements Validator<User>{

    @Override
    public void validate(User entity) throws ValidationException {
        validateFirstname(entity.getFirstname());
        validateLastname(entity.getLastname());
        validateEmail(entity.getEmail());
        validatePassword(entity.getPassword());
    }

    private void validateLastname(String nume) throws ValidationException {
        if(nume == null)
            throw new ValidationException("Numele nu poate sa fie null!");
        else if (nume.isEmpty())
            throw new ValidationException("Numele nu poate sa fie gol!");
    }

    private void validateFirstname(String prenume) throws ValidationException {
        if(prenume == null)
            throw new ValidationException("Prenumele nu poate sa fie null!");
        else if (prenume.isEmpty())
            throw new ValidationException("Prenumele nu poate sa fie gol!");
    }

    private void validateEmail(String email) throws ValidationException {
        if(email == null)
            throw new ValidationException("Emailul nu poate sa fie null!");
        else if(email.length()>100)
            throw new ValidationException("Emailul este prea lung!");
        else if(email.isEmpty())
            throw new ValidationException("Emailul nu poate sa fie gol!");
        else if(!email.contains("@"))
            throw new ValidationException("Emailul trebuie sa contina @ !");
        else if(email.indexOf("@")!=email.lastIndexOf("@"))
            throw new ValidationException("Emailul trebuie sa contina un singur @ !");
    }

    private void validatePassword(String pass) throws ValidationException{
        if(pass == null)
            throw new ValidationException("Parola nu poate sa fie null!");
        else if(pass.isEmpty())
            throw new ValidationException("Trebuie sa alegeti o parola!");
    }
}
