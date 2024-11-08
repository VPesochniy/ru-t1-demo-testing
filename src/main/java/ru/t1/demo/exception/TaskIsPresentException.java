package ru.t1.demo.exception;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class TaskIsPresentException extends RuntimeException {
    public TaskIsPresentException(String message) {
        super(message);
    }
}
