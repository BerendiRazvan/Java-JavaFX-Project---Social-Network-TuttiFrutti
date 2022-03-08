package com.example.map_proiect_extins.validators;

import com.example.map_proiect_extins.domain.Message;

public class MessageValidator implements Validator<Message> {
    @Override
    public void validate(Message entity) throws ValidationException {
        String error = "";
        if(entity.getMessage().equals("")){
            error += "The message must not be empty\n";
        }

        if(!error.equals(""))
            throw new ValidationException(error);
    }
}
