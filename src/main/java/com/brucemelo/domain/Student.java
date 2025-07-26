package com.brucemelo.domain;


public record Student(String firstName, String lastName, Integer age) {
    public boolean isAdult() {
        return age >= 18;
    }
}
