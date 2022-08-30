package com.springboot.w3.springboot_w3.Dto;

import com.springboot.w3.springboot_w3.Dto.model.Comment;
import com.springboot.w3.springboot_w3.Dto.model.Post;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CommentMypageDto {
    private Long id;
    private String name;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public CommentMypageDto(Comment comment){
        this.id = comment.getId();
        this.name = comment.getName();
        this.comment = comment.getComment();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
    }
}
