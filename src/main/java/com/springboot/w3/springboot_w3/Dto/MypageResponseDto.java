package com.springboot.w3.springboot_w3.Dto;

import com.springboot.w3.springboot_w3.Dto.model.Comment;
import com.springboot.w3.springboot_w3.Dto.model.Post;
import com.springboot.w3.springboot_w3.Dto.model.Recomment;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.List;

@Getter
@Setter
public class MypageResponseDto {

    List<PostMypageDto> userpost;
    List<CommentMypageDto> usercomment;
    List<RecommentMypageDto> userrecomment;

    List<PostMypageDto> userlikepost;
    List<CommentMypageDto> userlikecomment;
    List<RecommentMypageDto> userlikerecomment;

    public MypageResponseDto(List<PostMypageDto> userpost, List<CommentMypageDto> usercomment, List<RecommentMypageDto> userrecomment,
                             List<PostMypageDto> userlikepost, List<CommentMypageDto> userlikecomment, List<RecommentMypageDto> userlikerecomment){
        this.userpost = userpost;
        this.usercomment = usercomment;
        this.userrecomment = userrecomment;
        this.userlikepost = userlikepost;
        this.userlikecomment = userlikecomment;
        this.userlikerecomment = userlikerecomment;
    }
}
