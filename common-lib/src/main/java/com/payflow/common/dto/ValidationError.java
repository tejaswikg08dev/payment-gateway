package com.payflow.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationError {

    private String field;
    private String message;
    private Object rejectedValue;

    public static ValidationError of(String field, String message, Object rejectedValue){
        return ValidationError.builder().field(field).message(message).rejectedValue(rejectedValue).build();
    }
}
