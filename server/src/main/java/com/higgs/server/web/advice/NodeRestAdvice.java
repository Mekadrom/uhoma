package com.higgs.server.web.advice;

import com.higgs.server.web.HASResponse;
import com.higgs.server.web.exception.BadRequestException;
import com.higgs.server.web.exception.LinkedRoomsNotFoundException;
import com.higgs.server.web.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class NodeRestAdvice {
    @ExceptionHandler(value = { NotFoundException.class })
    protected ResponseEntity<String> notFound(final NotFoundException e) {
        return HASResponse.builder(null).status(HttpStatus.NOT_FOUND).error(e.getMessage()).build().toResponseEntity();
    }

    @ExceptionHandler(value = { LinkedRoomsNotFoundException.class })
    protected ResponseEntity<String> linkedRoomsNotFound(final LinkedRoomsNotFoundException e) {
        return HASResponse.builder(null).status(HttpStatus.NOT_FOUND).error(e.getMessage()).build().toResponseEntity();
    }

    @ExceptionHandler(value = { BadRequestException.class })
    protected ResponseEntity<String> badRequest(final BadRequestException e) {
        return HASResponse.builder(null).status(HttpStatus.BAD_REQUEST).error(e.getMessage()).build().toResponseEntity();
    }
}
