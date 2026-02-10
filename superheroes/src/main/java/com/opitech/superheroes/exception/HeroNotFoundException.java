package com.opitech.superheroes.exception;

public class HeroNotFoundException extends RuntimeException {
    public HeroNotFoundException(Long id) {
        super("Hero with id " + id + " not found");
    }
}
