package com.opitech.superheroes.exception;

public class HeroAlreadyExistsException extends RuntimeException {

    public HeroAlreadyExistsException(String name) {
        super("Hero with name '" + name + "' already exists");
    }
}