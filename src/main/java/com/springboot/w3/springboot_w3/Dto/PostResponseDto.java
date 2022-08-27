package com.springboot.w3.springboot_w3.Dto;

import com.springboot.w3.springboot_w3.Dto.model.Post;

import com.springboot.w3.springboot_w3.Dto.model.Timestamped;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;


@Getter
@Setter
public class PostResponseDto{
    private Long post_id;
    private String name;
    private String title;
    private String content;
    private int commentNum;
    private int likeNum;

    private LocalDateTime createdAt;
    // 마지막 수정일자임을 나타냅니다.
    private LocalDateTime modifiedAt;

    public PostResponseDto(Post post){
        this.post_id = post.getId();
        this.name = post.getName();
        this.createdAt = post.getCreatedAt();
        this.modifiedAt = post.getModifiedAt();
        this.likeNum = 0;
        this.commentNum = post.getComments().size();
        this.content = post.getContent();
        this.title = post.getTitle();
    }

    //id, 제목, 작성자, 좋아요 개수, 대댓글 제외한 댓글 개수, 등록일, 수정일 나타내기
}
