package ru.xing.springcourse.petproject3rest.util;

public class UserDoesNotHaveRoleException extends RuntimeException {
    public UserDoesNotHaveRoleException(String message) {
        super(message);
    }
}
