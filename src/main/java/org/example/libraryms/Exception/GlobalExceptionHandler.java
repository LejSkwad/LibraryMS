package org.example.libraryms.Exception;

import org.example.libraryms.Common.BaseResponse;
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

    @ExceptionHandler(BussinessException.class)
    public ResponseEntity<BaseResponse<Object>> handleBussinessException(BussinessException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse<>(null,exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Object>> handleMethodArgumentNotValidException (MethodArgumentNotValidException exception) {
        List<String> errorMessages = new ArrayList<>();

        for(FieldError error : exception.getBindingResult().getFieldErrors()) {
            errorMessages.add(error.getField() + ": " + error.getDefaultMessage());
        }
        String errorMessage = String.join(", ", errorMessages);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new BaseResponse<>(null, errorMessage));
    }
}
