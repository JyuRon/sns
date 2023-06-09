package com.example.exception;

import com.example.constant.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class SnsApplicationException extends RuntimeException{
    private final ErrorCode errorCode;
    private final String message;

    public SnsApplicationException(ErrorCode errorCode){
        this.errorCode = errorCode;
        this.message = null;
    }

    @Override
    public String getMessage() {
        if(message == null){
            return errorCode.getMessage();
        }
        return String.format("%s %s", errorCode.getMessage(), message);
    }
}
