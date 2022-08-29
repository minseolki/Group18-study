package com.springboot.w3.springboot_w3.Dto;

import com.springboot.w3.springboot_w3.Dto.model.Comment;
import com.springboot.w3.springboot_w3.Dto.model.Recomment;
import com.springboot.w3.springboot_w3.Dto.model.RecommentLike;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class RecommentResponseDto {
    private Long id;
    private String name;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private int likeNum;
    private List<LikeDto> recommentLikes;

    public RecommentResponseDto(Recomment recomment){
        this.id = recomment.getId();
        this.name = recomment.getName();
        this.comment = recomment.getRecomment();
        this.likeNum = 0;
        this.createdAt = recomment.getCreatedAt();
        this.modifiedAt = recomment.getModifiedAt();
    }
}