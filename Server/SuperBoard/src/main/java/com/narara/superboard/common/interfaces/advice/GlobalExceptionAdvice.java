package com.narara.superboard.common.interfaces.advice;

import com.narara.superboard.common.exception.InvalidFormatException;
import com.narara.superboard.common.exception.NotFoundException;
import com.narara.superboard.common.exception.TokenException;
import com.narara.superboard.common.interfaces.response.DefaultResponse;
import com.narara.superboard.common.interfaces.response.ResponseMessage;
import com.narara.superboard.common.interfaces.response.StatusCode;
import com.narara.superboard.member.exception.AccountDeletedException;
import com.narara.superboard.member.exception.AlreadyRegisteredLoginException;
import com.narara.superboard.member.exception.InvalidRefreshTokenException;
import io.lettuce.core.RedisException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionAdvice {

    // not found
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<?> handleIllegalArgumentException(NotFoundException ex) {
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.NOT_FOUND, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    // invalid(badrequest)
    @ExceptionHandler(InvalidFormatException.class)
    public ResponseEntity<?> handleIllegalArgumentException(InvalidFormatException ex) {
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.BAD_REQUEST, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    // 탈퇴한 계정
    @ExceptionHandler(AccountDeletedException.class)
    public ResponseEntity<?> handleIllegalArgumentException(AccountDeletedException ex) {
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.BAD_REQUEST, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    // refreshToken 유효성 에러
    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<?> handleIllegalArgumentException(InvalidRefreshTokenException ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token-Invalid", "");

        return new ResponseEntity<>(DefaultResponse.res(StatusCode.UNAUTHORIZED, ex.getMessage()), headers,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleIllegalArgumentException(Exception ex) {
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ResponseMessage.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 토큰 에러
    @ExceptionHandler(TokenException.class)
    public ResponseEntity<?> handleIllegalArgumentException(TokenException ex) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Token-Invalid", "");
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.UNAUTHORIZED, ex.getMessage()), headers,HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AlreadyRegisteredLoginException.class)
    public ResponseEntity<?> handleIllegalArgumentException(AlreadyRegisteredLoginException ex) {
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.BAD_REQUEST, ex.getMessage(),ex.getLoginType()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RedisException.class)
    public ResponseEntity<?> handleIllegalArgumentException(RedisException ex) {
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.BAD_REQUEST, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
