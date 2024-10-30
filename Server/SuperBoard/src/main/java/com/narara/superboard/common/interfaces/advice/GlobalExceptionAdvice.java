package com.narara.superboard.common.interfaces.advice;

import com.narara.superboard.common.exception.NotFoundException;
import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.common.interfaces.response.ResponseMessage;
import com.narara.superboard.common.interfaces.response.StatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleIllegalArgumentException(NotFoundException ex) {
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.NOT_FOUND, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

}
