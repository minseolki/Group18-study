package com.springboot.w3.springboot_w3.Service;

import com.springboot.w3.springboot_w3.Dto.*;
import com.springboot.w3.springboot_w3.Dto.model.*;
import com.springboot.w3.springboot_w3.Jwt.JwtTokenProvider;
import com.springboot.w3.springboot_w3.Repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final JwtTokenProvider jwtTokenProvider;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final RecommentRepository recommentRepository;
    private final PostLikeRepository postLikeRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final RecommentLikeRepository recommentLikeRepository;

    public ResponseEntity<ResponseModel> getMypage(HttpServletRequest httpServletRequest) {
        String token = httpServletRequest.getHeader("Authorization");

        if (token == null || !jwtTokenProvider.validateToken(token)) {
            ResponseModel responseModel = ResponseModel.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .httpStatus(HttpStatus.OK)
                    .message("토큰값이 없거나 유효하지 않습니다.").build();

            return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
        }

        String username = jwtTokenProvider.getUserPk(token);

        List<Post> posts = postRepository.findAll();
        List<PostMypageDto> userPost = new ArrayList<>();
        for(int a=0; a<posts.size(); a++){
            String author = posts.get(a).getName();
            if(author.equals(username))
                userPost.add(new PostMypageDto(posts.get(a)));
        }

        List<Comment> comments = commentRepository.findAll();
        List<CommentMypageDto> userComment =  new ArrayList<>();
        for(int a=0; a<comments.size(); a++){
            String author = comments.get(a).getName();
            if (author.equals(username))
                userComment.add(new CommentMypageDto(comments.get(a)));
        }

        List<Recomment> recomments = recommentRepository.findAll();
        List<RecommentMypageDto> userRecomment = new ArrayList<>();
        for (int a=0; a<recomments.size(); a++){
            String author = recomments.get(a).getName();
            if (author.equals(username))
                userRecomment.add(new RecommentMypageDto(recomments.get(a)));
        }

        List<PostMypageDto> userLikePost = new ArrayList<>();
        List<PostLike> postLikes = postLikeRepository.findAll();
        for (int a=0; a<postLikes.size(); a++){
            String author = postLikes.get(a).getName();
            if (author.equals(username)){
                Post post = postLikes.get(a).getPost();
                userLikePost.add(new PostMypageDto(post));
            }
        }

        List<CommentMypageDto> userLikeComment = new ArrayList<>();
        List<CommentLike> commentLikes = commentLikeRepository.findAll();
        for (int a=0; a<commentLikes.size(); a++){
            String author = commentLikes.get(a).getName();
            if (author.equals(username)){
                Comment comment = commentLikes.get(a).getComment();
                userLikeComment.add(new CommentMypageDto(comment));
            }
        }

        List<RecommentMypageDto> userLikeRecomment = new ArrayList<>();
        List<RecommentLike> recommentLikes = recommentLikeRepository.findAll();
        for (int a=0; a<recommentLikes.size(); a++){
            String author = recommentLikes.get(a).getName();
            if (author.equals(username)){
                Recomment recomment = recommentLikes.get(a).getRecomment();
                userLikeRecomment.add(new RecommentMypageDto(recomment));
            }
        }

        MypageResponseDto responseDto = new MypageResponseDto(userPost, userComment, userRecomment, userLikePost, userLikeComment, userLikeRecomment);
        List<MypageResponseDto> mypageResponseDtos = new ArrayList<>();
        mypageResponseDtos.add(responseDto);

        ResponseModel responseModel = ResponseModel.builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("마이페이지 조회 성공")
                .data(mypageResponseDtos).build();

        return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
    }
}
