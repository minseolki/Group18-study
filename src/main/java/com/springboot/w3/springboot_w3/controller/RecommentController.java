package com.springboot.w3.springboot_w3.controller;

import com.springboot.w3.springboot_w3.Dto.CommentRequestDto;
import com.springboot.w3.springboot_w3.Dto.ResponseModel;
import com.springboot.w3.springboot_w3.Service.RecommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class RecommentController {

    private final RecommentService recommentService;

    @GetMapping("/api/comment/{comment_id}/recomment")
    public ResponseEntity<ResponseModel> getAllRecomment(@PathVariable Long comment_id){
        return recommentService.getAllRecomment(comment_id);
    }

    @PostMapping("/api/auth/comment/{comment_id}/recomment")
    public ResponseEntity<ResponseModel> postRecomment(@PathVariable Long comment_id,
                                                       @RequestBody CommentRequestDto requestDto, HttpServletRequest request){
        return recommentService.postRecomment(comment_id, requestDto, request);
    }

    @PutMapping("/api/auth/comment/recomment/{recomment_id}")
    public ResponseEntity<ResponseModel> putRecomment(@PathVariable Long recomment_id, @RequestBody CommentRequestDto requestDto,
                                                      HttpServletRequest request){
        return recommentService.putRecomment(recomment_id, requestDto, request);
    }

    @DeleteMapping("/api/auth/comment/recomment/{recomment_id}")
    public ResponseEntity<ResponseModel> deleteRecomment(@PathVariable Long recomment_id, HttpServletRequest request){
        return recommentService.deleteRecomment(recomment_id, request);
    }

    //해당 대댓글 좋아요 추가
    @PutMapping("/api/auth/recomment/{recomment_id}/like/true")
    public ResponseEntity<ResponseModel> putRecommentLikeTrue(@PathVariable Long recomment_id, HttpServletRequest request){
        return recommentService.putCommentLikeTrue(recomment_id, request);
    }

    @PutMapping("/api/auth/recomment/{recomment_id}/like/false")
    public ResponseEntity<ResponseModel> putReCommentLikeFalse(@PathVariable Long recomment_id, HttpServletRequest request){
        return recommentService.putCommentLikeFalse(recomment_id, request);
    }
}
