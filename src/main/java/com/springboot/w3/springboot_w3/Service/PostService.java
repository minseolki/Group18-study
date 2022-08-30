package com.springboot.w3.springboot_w3.Service;


import com.springboot.w3.springboot_w3.Dto.*;
import com.springboot.w3.springboot_w3.Dto.model.*;
import com.springboot.w3.springboot_w3.Jwt.JwtTokenProvider;
import com.springboot.w3.springboot_w3.Repository.*;
import com.springboot.w3.springboot_w3.Dto.ResponseModel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
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
    private final RecommentRepository recommentRepository;
    private final RecommentLikeRepository recommentLikeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final FileUploaderService fileUploaderService;

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


    public ResponseEntity<ResponseModel> postPostService(PostRequestDto requestDto, HttpServletRequest request, MultipartFile multipartFile) {

        String token = request.getHeader("Authorization");

        if (token == null || !jwtTokenProvider.validateToken(token)) {
            ResponseModel responseModel = ResponseModel.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .httpStatus(HttpStatus.OK)
                    .message("토큰값이 없거나 유효하지 않습니다.").build();

            return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
        }


        String url = fileUploaderService.uploadImage(multipartFile);
        String username = jwtTokenProvider.getUserPk(token);
        Post post = new Post(requestDto, username);
        post.setImgUrl(url);
        postRepository.save(post); // 게시글 저장 -> 데이터베이스

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

        List<CommentLike> commentLikes = commentLikeRepository.findAll();
        List<RecommentLike> recommentLikes = recommentLikeRepository.findAll();
        List<Recomment> recomment = recommentRepository.findAll();

        List<CommentResponseDto> commentResponseDtoList = new ArrayList<>();

        for(int a=0; a<comments.size(); a++){
            CommentResponseDto responseDto = new CommentResponseDto(comments.get(a));

            List<LikeDto> commentLike = new ArrayList<>();
            //댓글 좋아요
            for(int e=0; e<commentLikes.size(); e++){
                if(commentLikes.get(e).getComment().getId() == comments.get(a).getId()){
                    commentLike.add(new LikeDto(commentLikes.get(e)));
                }
            }
            responseDto.setLikeNum(commentLike.size());
            //responseDto.setComment_like_list(commentLike);

            List<RecommentResponseDto> recommentResponseDtos = new ArrayList<>();
            for (int c=0; c<recomment.size(); c++){

                if (recomment.get(c).getComment().getId() == comments.get(a).getId()){
                    RecommentResponseDto responseDto1 = new RecommentResponseDto(recomment.get(c));
                    List<LikeDto> recommentDto = new ArrayList<>();
                    //대댓글에 해당하는 좋아요 유저
                    for (int d=0; d<recommentLikes.size(); d++){
                        if(recomment.get(c).getId() == recommentLikes.get(d).getRecomment().getId()){
                            LikeDto likeDto = new LikeDto(recommentLikes.get(d));
                            recommentDto.add(likeDto);
                        }
                    }
                    responseDto1.setLikeNum(recommentDto.size());
                    //responseDto1.setRecommentLikes(recommentDto);
                    recommentResponseDtos.add(responseDto1);
                }
            }
            responseDto.setRecommentNum(recommentResponseDtos.size());
            responseDto.setRecomment(recommentResponseDtos);
            commentResponseDtoList.add(responseDto);
        }

        PostDetailResponseDto postResponseDto = new PostDetailResponseDto(post);
        postResponseDto.setComment(commentResponseDtoList);
        postResponseDto.setCommentNum(commentResponseDtoList.size());

        //게시글 좋아요 가져오기
        List<PostLike> likes = postLikeRepository.findAll();
        List<LikeDto> likeDtos = new ArrayList<>();

        for (int a=0; a<likes.size(); a++){
            if(likes.get(a).getPost().getId() == post.getId())
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
    public ResponseEntity<ResponseModel> putPostLikeTrue(Long post_id, HttpServletRequest request) {
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

        for (int a = 0; a < postLikes.size(); a++) {
            if (postLikes.get(a).getName().equals(username) && post_id == postLikes.get(a).getPost().getId()) {
                ResponseModel responseModel = ResponseModel.builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .httpStatus(HttpStatus.OK)
                        .message("이미 해당 게시글에 좋아요가 되었습니다.").build();

                return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
            }
        }

        List<PostLike> postLikes1 = post.getPostLikes();

        PostLike postLike = new PostLike(username, post);
        postLikeRepository.save(postLike);

        postLikes1.add(postLike);

        post.updateLikeNum(postLikes1.size());
        post.setPostLikes(postLikes1);

        ResponseModel responseModel = ResponseModel.builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("like success!").build();
        return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
    }

    public ResponseEntity<ResponseModel> putPostLikeFalse(Long post_id, HttpServletRequest request) {

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

        for (int a = 0; a < postLikes.size(); a++) {
            System.out.println(postLikes.get(a).getName());
            System.out.println(post_id);
            System.out.println(postLikes.get(a).getPost().getId());
            if (postLikes.get(a).getName().equals(username) && post_id == postLikes.get(a).getPost().getId()) {

                postLikeRepository.delete(postLikes.get(a)); // 해당 좋아요 삭제
                post.setLikeNum(post.getLikeNum()-1);

                ResponseModel responseModel = ResponseModel.builder()
                        .code(HttpStatus.OK.value())
                        .httpStatus(HttpStatus.OK)
                        .message("like cancel success !").build();

                return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
            }
        }

        ResponseModel responseModel = ResponseModel.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message("해당 게시글에 좋아요가 되어있지 않습니다.").build();
        return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
    }


}