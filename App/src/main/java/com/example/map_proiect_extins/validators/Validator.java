package com.example.map_proiect_extins.validators;

public interface Validator<T> {
    void validate(T entity) throws ValidationException;
}