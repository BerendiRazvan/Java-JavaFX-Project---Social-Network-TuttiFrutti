package com.example.map_proiect_extins.validators;

import com.example.map_proiect_extins.domain.Event;



public class EventsValidator implements Validator<Event>{

    @Override
    public void validate(Event entity) throws ValidationException {
        String error = "";

        if(entity.getStartDate().isAfter(entity.getFinishDate()))
            error+="Start date cant be higher than finish date!\nFinish date can not be lower than start date!\n";

        if(!error.equals(""))
            throw new ValidationException(error);
    }
}
