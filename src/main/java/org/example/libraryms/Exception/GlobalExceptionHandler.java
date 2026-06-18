package org.example.libraryms.Exception;

import org.example.libraryms.Common.BaseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BussinessException.class)
    public ResponseEntity<BaseResponse<Object>> handleBussinessException(BussinessException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse<>(null, exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        List<String> errorMessages = new ArrayList<>();
        for (FieldError error : exception.getBindingResult().getFieldErrors()) {
            errorMessages.add(error.getField() + ": " + error.getDefaultMessage());
        }
        String errorMessage = String.join(", ", errorMessages);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse<>(null, errorMessage));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponse<Object>> handleGenericException(Exception exception) {
        log.error("Unexpected error", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new BaseResponse<>(null, "Đã xảy ra lỗi, vui lòng thử lại sau"));
    }
}
