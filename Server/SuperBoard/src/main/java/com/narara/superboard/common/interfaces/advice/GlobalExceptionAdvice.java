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
import com.narara.superboard.workspacemember.exception.EmptyWorkspaceMemberException;
import io.lettuce.core.RedisException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
/**
 * 가장 구체적인 핸들러부터 실행
 */
@ControllerAdvice
public class GlobalExceptionAdvice {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex) {
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.FORBIDDEN, ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(EmptyWorkspaceMemberException.class)
    public ResponseEntity<?> handleEmptyWorkspaceMemberException(EmptyWorkspaceMemberException ex) {
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.BAD_REQUEST, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    //잘못된 입력
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleAccessDeniedException(HttpMessageNotReadableException ex) {
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.BAD_REQUEST, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

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

        return new ResponseEntity<>(DefaultResponse.res(StatusCode.UNAUTHORIZED, ex.getMessage()), headers, HttpStatus.UNAUTHORIZED);
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
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.UNAUTHORIZED, "유효하지 않은 JWT 토큰 때문에 예외가 발생했습니다. \n" + ex.getMessage()), headers, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(AlreadyRegisteredLoginException.class)
    public ResponseEntity<?> handleIllegalArgumentException(AlreadyRegisteredLoginException ex) {
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.BAD_REQUEST, ex.getMessage(), ex.getLoginType()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RedisException.class)
    public ResponseEntity<?> handleIllegalArgumentException(RedisException ex) {
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.BAD_REQUEST, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(Exception ex) {
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(Exception ex) {
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.INTERNAL_SERVER_ERROR, ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointerException(Exception ex) {
        return new ResponseEntity<>(DefaultResponse.res(StatusCode.BAD_REQUEST, ex.getMessage()) + "\n 혹시, dto를 보새실때, 대소문자를 잘못하시진 않으셨는지 확인해보세요", HttpStatus.BAD_REQUEST);
    }

}
