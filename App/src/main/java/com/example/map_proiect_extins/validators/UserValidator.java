package com.example.map_proiect_extins.validators;

import com.example.map_proiect_extins.domain.User;

public class UserValidator implements Validator<User> {
    @Override
    public void validate(User entity) throws ValidationException {
        String error = "";

        if (!entity.getLastName().matches("[a-zA-Z]+")) {
            error += "Last name must contain only letters!\n";
        }

        if (!entity.getFirstName().matches("[a-zA-Z]+")) {
            error += "First name must contain only letters!\n";
        }

        if (entity.getFirstName().equals("")) {
            error += "First name must not be empty!\n";
        }

        if (entity.getLastName().equals("")) {
            error += "Last name must not be empty!\n";
        }

        if (entity.getEmail().length()==0 || !entity.getEmail().contains(".") || !entity.getEmail().contains("@") || entity.getEmail().contains(" ")) {
            error += "Email invalid!\n";
        }

        if (entity.getPassword().length() < 8) {
            error += "Password should be at least 8 characters long!\n";
        }

        if (!error.equals(""))
            throw new ValidationException(error);
    }
}
