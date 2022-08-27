package com.springboot.w3.springboot_w3.controller;


import com.springboot.w3.springboot_w3.Dto.CommentRequestDto;
import com.springboot.w3.springboot_w3.Service.CommentService;
import com.springboot.w3.springboot_w3.Dto.ResponseModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class CommentController {

    @Autowired
    public CommentService commentService;


    //해당 게시글 댓글 가져오기
    @GetMapping("/api/comment/{id}")
    public ResponseEntity<ResponseModel> getComment(@PathVariable Long id){
        return commentService.getAllComment(id);
    }



    //댓글 작성
    @PostMapping("/api/auth/post/{postId}/comment")
    public ResponseEntity<ResponseModel> postComment(HttpServletRequest request, @RequestBody CommentRequestDto requestDto, @PathVariable Long postId){
        return commentService.postCommentService(request, requestDto, postId);
    }

    //댓글 수정
    @PutMapping("/api/auth/comment/{id}")
    public ResponseEntity<ResponseModel> putComment(HttpServletRequest request,
                                                    @RequestBody CommentRequestDto requestDto, @PathVariable Long id){
        return commentService.putCommentService(request, requestDto, id);
    }

    @DeleteMapping("/api/auth/comment/{id}")
    public ResponseEntity<ResponseModel> DeleteComment(HttpServletRequest request,
                                                       @PathVariable Long id){
        return commentService.DeleteCommentService(request,id);
    }

}
