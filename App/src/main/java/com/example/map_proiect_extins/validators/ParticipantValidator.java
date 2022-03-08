package com.example.map_proiect_extins.validators;

import com.example.map_proiect_extins.domain.EventParticipant;
import com.example.map_proiect_extins.domain.Friendship;

public class ParticipantValidator  implements Validator<EventParticipant>{
    @Override
    public void validate(EventParticipant entity) throws ValidationException {
        String error = "";
        //no validation for participant for now

        if(!error.equals(""))
            throw new ValidationException(error);
    }
}
