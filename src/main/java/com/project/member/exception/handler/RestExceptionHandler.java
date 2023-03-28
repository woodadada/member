package com.project.member.exception.handler;


import com.project.member.constant.ErrorCode;
import com.project.member.exception.ErrorResponse;
import com.project.member.exception.SiteException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@RestControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = { SiteException.class })
    protected ResponseEntity<ErrorResponse> handleCustomException(SiteException e) {
        log.error("handleCustomException throw CustomException : {}", e.getErrorCode());
        return ErrorResponse.toResponseEntity(e.getErrorCode());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
        // 에러 메세지 문자열 가공 field : message 형태
        List<String> errorList = e
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + " : " +  fieldError.getDefaultMessage())
                .collect(Collectors.toList());

        log.error("handleMethodArgumentNotValid throw CustomException : {}", e);

        return ErrorResponse.toResponseEntity(ErrorCode.VALIDATION_EXCEPTION, errorList.toString());
    }
}
