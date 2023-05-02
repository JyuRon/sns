package com.example.dto.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Response<T> {
    private String resultCode;
    private T result;

    public static Response<Void> error(String errorCode){
        return new Response<>(errorCode, null);
    }

    public static Response<Void> success(){
        return new Response<>("SUCCESS", null);
    }

    public static <T> Response<T> success(T result){
        return new Response<>("SUCCESS", result);
    }

    public String toStream() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(this);
    }
}
