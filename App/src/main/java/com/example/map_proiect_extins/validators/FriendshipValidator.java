package com.example.map_proiect_extins.validators;

import com.example.map_proiect_extins.domain.Friendship;

public class FriendshipValidator implements Validator<Friendship> {
    @Override
    public void validate(Friendship entity) throws ValidationException {
        String error = "";
        if(entity.getId().getFirst()<=0 || entity.getId().getSecond()<=0){
            error+="id must be greater than 0";
        }
        if(!error.equals(""))
            throw new ValidationException(error);
    }
}
