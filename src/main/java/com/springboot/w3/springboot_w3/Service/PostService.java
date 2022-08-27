package com.springboot.w3.springboot_w3.Service;


import com.springboot.w3.springboot_w3.Dto.*;
import com.springboot.w3.springboot_w3.Dto.model.PostLike;
import com.springboot.w3.springboot_w3.Jwt.JwtTokenProvider;
import com.springboot.w3.springboot_w3.Repository.PostLikeRepository;
import com.springboot.w3.springboot_w3.Repository.PostRepository;
import com.springboot.w3.springboot_w3.Dto.model.Comment;
import com.springboot.w3.springboot_w3.Dto.model.Post;
import com.springboot.w3.springboot_w3.Dto.ResponseModel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service // 스프링에게 이 클래스는 서비스임을 명시
public class PostService {

    // final: 서비스에게 꼭 필요한 녀석임을 명시
    private final PostRepository postRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final CommentService commentService;
    private final PostLikeRepository postLikeRepository;

    public ResponseEntity<ResponseModel> getPostAllService() {
        int num = (int) postRepository.count();
        if (num != 0) {
            List<Post> posts = postRepository.findAll();
            List<PostResponseDto> postResponseDtos = new ArrayList<>();

            for(int a=0; a<posts.size(); a++){
                int id = posts.get(a).getId().intValue();
                List<Comment> comments = commentService.getComment(id);
                posts.get(a).setComments(comments);
                PostResponseDto responseDto = new PostResponseDto(posts.get(a));
                postResponseDtos.add(responseDto);
            }

            ResponseModel responseModel = ResponseModel.builder()
                    .code(HttpStatus.OK.value())
                    .httpStatus(HttpStatus.OK)
                    .message("전체 게시글 조회 성공")
                    .data(new ArrayList<>(postResponseDtos)).build();
            return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
        } else {

            ResponseModel responseModel = ResponseModel.builder()
                    .code(HttpStatus.NO_CONTENT.value())
                    .httpStatus(HttpStatus.OK)
                    .message("게시글이 없습니다. 생성해 주세요.").build();
            return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
        }
    }


    public ResponseEntity<ResponseModel> postPostService(PostRequestDto requestDto, HttpServletRequest request) {

        String token = request.getHeader("Authorization");
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            ResponseModel responseModel = ResponseModel.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .httpStatus(HttpStatus.OK)
                    .message("토큰값이 없거나 유효하지 않습니다.").build();

            return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
        }

        String username = jwtTokenProvider.getUserPk(token);
        Post post = new Post(requestDto, username);
        postRepository.save(post);
        List<Post> pp = new ArrayList<>();
        pp.add(post);
        ResponseModel responseModel = ResponseModel.builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("게시글 생성 완료")
                .data(new ArrayList<>(pp)).build();

        return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());

    }

    public ResponseEntity<ResponseModel> getPostIdService(Long id){
        int int_id= id.intValue();
        Post post = postRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 아이디가 존재하지 않습니다.")
        );


        List<Comment> comments = commentService.getComment(int_id);

        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();
        List<LikeDto> likeDtos = new ArrayList<>();

        for(int a=0; a<comments.size(); a++){
            CommentResponseDto responseDto = new CommentResponseDto(comments.get(a));
            commentResponseDtoList.add(responseDto);
        }

        PostDetailResponseDto postResponseDto = new PostDetailResponseDto(post);
        postResponseDto.setComment(commentResponseDtoList);
        postResponseDto.setCommentNum(commentResponseDtoList.size());

        List<PostLike> likes = post.getPostLikes();

        for (int a=0; a<likes.size(); a++){
            likeDtos.add(new LikeDto(likes.get(a)));
        }
        postResponseDto.setLikeDtoList(likeDtos);
        postResponseDto.setLikeNum(likeDtos.size());
        post.updateLikeNum(likeDtos.size());
        post.updateCommentNum(commentResponseDtoList.size());
        postRepository.save(post);


        ArrayList<PostDetailResponseDto> postss = new ArrayList<>();
        postss.add(postResponseDto);

        ResponseModel responseModel = ResponseModel.builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("게시글 상세 조회 완료")
                .data(new ArrayList<>(postss)).build();

        return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
    }

    public ResponseEntity<ResponseModel> putPostService(Long id, PostRequestDto postRequestDto,
                                                        HttpServletRequest request){
        Post post = postRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 아이디가 존재하지 않습니다.")
        );

        String user = post.getName();
        String token = request.getHeader("Authorization");
        if (token == null || !jwtTokenProvider.validateToken(token)){
            ResponseModel responseModel = ResponseModel.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .httpStatus(HttpStatus.OK)
                    .message("토큰값이 없거나 유효하지 않습니다.").build();

            return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
        }

        String username = jwtTokenProvider.getUserPk(token);

        if (user.equals(username)) {
            post.update(postRequestDto);

            ArrayList<Post> postss = new ArrayList<>();
            postss.add(post);

            ResponseModel responseModel = ResponseModel.builder()
                    .code(HttpStatus.OK.value())
                    .httpStatus(HttpStatus.OK)
                    .message("게시글 수정 완료")
                    .data(new ArrayList<>(postss)).build();

            return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
        }else{
            ResponseModel responseModel = ResponseModel.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .httpStatus(HttpStatus.OK)
                    .message("사용자가 일치하지 않습니다.").build();

            return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
        }
    }

    public ResponseEntity<ResponseModel> deletePostService(Long id, HttpServletRequest request){
        Post post = postRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("해당 아이디가 존재하지 않습니다.")
        );
        String user = post.getName();
        String token = request.getHeader("Authorization");
        if (token == null || !jwtTokenProvider.validateToken(token)){
            ResponseModel responseModel = ResponseModel.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .httpStatus(HttpStatus.OK)
                    .message("토큰값이 없거나 유효하지 않습니다.").build();

            return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
        }

        String username = jwtTokenProvider.getUserPk(token);

        if (user.equals(username)) {
            postRepository.deleteById(id);

            ResponseModel responseModel = ResponseModel.builder()
                    .code(HttpStatus.OK.value())
                    .httpStatus(HttpStatus.OK)
                    .message("게시글 삭제 완료").build();

            return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
        }else{
            ResponseModel responseModel = ResponseModel.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .httpStatus(HttpStatus.OK)
                    .message("사용자가 일치하지 않습니다.").build();

            return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
        }

    }

    //수정하기 --
    public ResponseEntity<ResponseModel> getPostLikeTrue(Long post_id, HttpServletRequest request) {
        Post post = postRepository.findById(post_id).orElseThrow(
                () -> new IllegalArgumentException("해당 아이디가 존재하지 않습니다.")
        );

        String token = request.getHeader("Authorization");
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            ResponseModel responseModel = ResponseModel.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .httpStatus(HttpStatus.OK)
                    .message("토큰값이 없거나 유효하지 않습니다.").build();

            return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
        }

        String username = jwtTokenProvider.getUserPk(token);
        List<PostLike> postLikes = postLikeRepository.findAll();
        List<LikeDto> likeDtos = new ArrayList<>();

        for (int a = 0; a < postLikes.size(); a++) {
            if (postLikes.get(a).getName().equals(username) && post_id == postLikes.get(a).getPost().getId()) {
                ResponseModel responseModel = ResponseModel.builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .httpStatus(HttpStatus.OK)
                        .message("이미 해당 게시글에 좋아요가 되었습니다.").build();

                return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
            }
        }

        for (int b = 0; b < postLikes.size(); b++) {
            if (postLikes.get(b).getPost().getId() == post_id) {
                likeDtos.add(new LikeDto(postLikes.get(b)));
            }
        }

        PostLike postLike = new PostLike(username, post);
        postLikeRepository.save(postLike);

        int likenum = post.getLikeNum() + 1;
        System.out.println(likenum);
        post.updateLikeNum(likenum);
        postRepository.save(post);
        PostDetailResponseDto postDetailResponseDto = new PostDetailResponseDto(post);
        postDetailResponseDto.setLikeDtoList(likeDtos);

        List<PostDetailResponseDto> posts = new ArrayList<>();
        posts.add(postDetailResponseDto);

        ResponseModel responseModel = ResponseModel.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .httpStatus(HttpStatus.OK)
                .message("like success!")
                .data(new ArrayList<>(posts)).build();
        return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
    }

    public ResponseEntity<ResponseModel> getPostLikeFalse(Long post_id, HttpServletRequest request) {
        Post post = postRepository.findById(post_id).orElseThrow(
                () -> new IllegalArgumentException("해당 아이디가 존재하지 않습니다.")
        );

        String token = request.getHeader("Authorization");
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            ResponseModel responseModel = ResponseModel.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .httpStatus(HttpStatus.OK)
                    .message("토큰값이 없거나 유효하지 않습니다.").build();

            return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
        }

        String username = jwtTokenProvider.getUserPk(token);
        List<PostLike> postLikes = postLikeRepository.findAll();
        List<LikeDto> likeDtos = new ArrayList<>();

        for (int a = 0; a < postLikes.size(); a++) {
            if (postLikes.get(a).getName().equals(username) && post_id == postLikes.get(a).getPost().getId()) {
                ResponseModel responseModel = ResponseModel.builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .httpStatus(HttpStatus.OK)
                        .message("이미 해당 게시글에 좋아요가 되었습니다.").build();

                return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
            }
        }

        for (int b = 0; b < postLikes.size(); b++) {
            if (postLikes.get(b).getPost().getId() == post_id) {
                likeDtos.add(new LikeDto(postLikes.get(b)));
            }
        }

        PostLike postLike = new PostLike(username, post);
        postLikeRepository.save(postLike);

        int likenum = post.getLikeNum() - 1;
        System.out.println(likenum);
        post.updateLikeNum(likenum);
        postRepository.save(post);
        PostDetailResponseDto postDetailResponseDto = new PostDetailResponseDto(post);
        postDetailResponseDto.setLikeDtoList(likeDtos);

        List<PostDetailResponseDto> posts = new ArrayList<>();
        posts.add(postDetailResponseDto);

        ResponseModel responseModel = ResponseModel.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .httpStatus(HttpStatus.OK)
                .message("like cancel success!")
                .data(new ArrayList<>(posts)).build();
        return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
    }

}