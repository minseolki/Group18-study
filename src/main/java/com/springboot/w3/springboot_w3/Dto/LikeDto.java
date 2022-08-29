package com.springboot.w3.springboot_w3.Dto;

import com.springboot.w3.springboot_w3.Dto.model.CommentLike;
import com.springboot.w3.springboot_w3.Dto.model.PostLike;
import com.springboot.w3.springboot_w3.Dto.model.RecommentLike;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LikeDto {
    private String name;

    public LikeDto(PostLike postLike){
        this.name = postLike.getName();
    }

    public LikeDto(CommentLike commentLike){
        this.name = commentLike.getName();
    }

    public LikeDto(RecommentLike recommentLike){
        this.name = recommentLike.getName();
    }
}
