package com.momentum.momentum.exception;
import com.momentum.momentum.service.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

public class GlobalExceptionHandler {

    @ExceptionHandler(JwtUtil.JwtTokenExpiredException.class)
    public ResponseEntity<String> handleJwtTokenExpiredException(JwtUtil.JwtTokenExpiredException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

}
