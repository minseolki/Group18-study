package com.springboot.w3.springboot_w3.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseModel {

    private Integer code;
    private HttpStatus httpStatus;
    private String message;
    private List<Object> data;
}
