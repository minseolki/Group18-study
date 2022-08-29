package com.springboot.w3.springboot_w3.Dto;

import com.springboot.w3.springboot_w3.Dto.model.Comment;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class CommentResponseDto {
    private Long id;
    private String name;
    private String comment;
    private int recommentNum;
    private List<RecommentResponseDto> recomment;
    private int likeNum;
    private List<LikeDto> comment_like_list;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public CommentResponseDto(Comment comment){
        this.id = comment.getId();
        this.name = comment.getName();
        this.comment = comment.getComment();
        this.likeNum = 0;
        this.recommentNum = 0;
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
    }
}
