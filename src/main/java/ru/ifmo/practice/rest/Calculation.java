package ru.ifmo.practice.rest;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonInclude(Include.NON_NULL)
public class Calculation {
    private String request;
    private String response;
    private LocalDateTime timestamp;
}
