package com.springboot.w3.springboot_w3.Service;

import com.springboot.w3.springboot_w3.Dto.LikeDto;
import com.springboot.w3.springboot_w3.Dto.model.CommentLike;
import com.springboot.w3.springboot_w3.Dto.model.PostLike;
import com.springboot.w3.springboot_w3.Dto.model.RecommentLike;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LikeService {

    public List<LikeDto> getRecommentLikeList(List<RecommentLike> recommentLikes){

        List<LikeDto> recommentLikeList = new ArrayList<>();

        for (RecommentLike recommentLike : recommentLikes){
            recommentLikeList.add(new LikeDto(recommentLike));
        }
        return recommentLikeList;
    }

    public List<LikeDto> getCommentLikeList(List<CommentLike> commentLikeList){
        List<LikeDto> commentLikes = new ArrayList<>();

        for (CommentLike commentLike : commentLikeList){
            commentLikes.add(new LikeDto(commentLike));
        }

        return commentLikes;
    }

    public List<LikeDto> getPostLikeList(List<PostLike> postLikeList){
        List<LikeDto> postLikes = new ArrayList<>();

        for(PostLike postLike : postLikeList){
            postLikes.add(new LikeDto(postLike));
        }
        return postLikes;
    }
}
