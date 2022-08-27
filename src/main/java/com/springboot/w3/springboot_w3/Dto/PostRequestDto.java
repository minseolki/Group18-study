package com.springboot.w3.springboot_w3.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PostRequestDto {
    private String title;
    private String content;
    private String imgUrl;
}
