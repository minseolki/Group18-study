package com.springboot.w3.springboot_w3.controller;


import com.springboot.w3.springboot_w3.Dto.PostRequestDto;
import com.springboot.w3.springboot_w3.Service.PostService;
import com.springboot.w3.springboot_w3.Dto.ResponseModel;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class PostController {

    @Autowired
    private PostService postService;

    @GetMapping("/api/post")
    public ResponseEntity<ResponseModel> getPostAll(){
        return postService.getPostAllService();
    }



    @PostMapping(value = "/api/auth/post", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ResponseModel> postPost(@RequestPart PostRequestDto requestDto,
                                                  @RequestPart MultipartFile multipartFile, HttpServletRequest request){
        return postService.postPostService(requestDto, request, multipartFile);
    }


    //게시글 상세 조회
    @GetMapping("/api/post/{post_id}")
    public ResponseEntity<ResponseModel> getPostId(@PathVariable Long post_id){
        return postService.getPostIdService(post_id);
    }


    //게시글 수정
    @PutMapping("/auth/post/{post_id}")
    public ResponseEntity<ResponseModel> putPost(@PathVariable Long post_id, @RequestBody PostRequestDto postRequestDto, HttpServletRequest request){
        return postService.putPostService(post_id, postRequestDto, request);
    }

    @DeleteMapping("/auth/post/{post_id}")
    public ResponseEntity<ResponseModel> deletePost(@PathVariable Long post_id, HttpServletRequest request){
        return postService.deletePostService(post_id, request);
    }

    //해당 게시글 좋아요 추가
    @PutMapping("/api/auth/post/{post_id}/like/true")
    public ResponseEntity<ResponseModel> putPostLikeTrue(@PathVariable Long post_id, HttpServletRequest request){
        return postService.putPostLikeTrue(post_id, request);
    }
    @PutMapping("/api/auth/post/{post_id}/like/false")
    public ResponseEntity<ResponseModel> putPostLikeFalse(@PathVariable Long post_id, HttpServletRequest request){
        return postService.putPostLikeFalse(post_id, request);
    }

}
