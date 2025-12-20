package com.sean.ninnong.common;

import com.sean.ninnong.common.ErrorResponse;
import com.sean.ninnong.exception.InvalidRefreshTokenException;
import com.sean.ninnong.exception.RefreshTokenMismatchException;
import com.sean.ninnong.exception.RefreshTokenNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            InvalidRefreshTokenException.class,
            RefreshTokenNotFoundException.class,
            RefreshTokenMismatchException.class
    })
    public ResponseEntity<ErrorResponse> handleAuthExceptions(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("AUTH_ERROR", ex.getMessage()));
    }
}
