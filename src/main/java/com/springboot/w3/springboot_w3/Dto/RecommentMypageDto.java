package com.springboot.w3.springboot_w3.Dto;

import com.springboot.w3.springboot_w3.Dto.model.Comment;
import com.springboot.w3.springboot_w3.Dto.model.Recomment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RecommentMypageDto {
    private Long id;
    private String name;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public RecommentMypageDto(Recomment recomment){
        this.id = recomment.getId();
        this.name = recomment.getName();
        this.comment = recomment.getRecomment();
        this.createdAt = recomment.getCreatedAt();
        this.modifiedAt = recomment.getModifiedAt();
    }
}
