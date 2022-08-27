package com.springboot.w3.springboot_w3.Dto;

import com.springboot.w3.springboot_w3.Dto.model.PostLike;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikeDto {
    private String name;

    public LikeDto(PostLike postLike){
        this.name = postLike.getName();
    }
}
