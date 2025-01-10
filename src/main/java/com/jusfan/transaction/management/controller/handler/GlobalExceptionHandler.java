package com.jusfan.transaction.management.controller.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.jusfan.transaction.management.model.ApiResponse;
import com.jusfan.transaction.management.model.ErrorCode;


/**
 * @author jiasifan
 * Created on 2025-01-10
 * 用于处理全局异常
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
        // 最外层封个错误码 可根据业务和不同接口调整
        return new ResponseEntity<>(new ApiResponse<>(ErrorCode.ERROR.getCode(), ex.getMessage()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}