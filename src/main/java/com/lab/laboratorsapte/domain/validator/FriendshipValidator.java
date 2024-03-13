package com.lab.laboratorsapte.domain.validator;

import com.lab.laboratorsapte.domain.Friendship;

public class FriendshipValidator implements Validator<Friendship> {

    @Override
    public void validate(Friendship entity) throws ValidationException {
        if(entity.getId().getLeft().equals(entity.getId().getRight()))
            throw  new ValidationException("Prietenia trebuie sa contina doi useri diferiti!");
    }
}
