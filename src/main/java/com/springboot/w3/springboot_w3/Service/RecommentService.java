package com.springboot.w3.springboot_w3.Service;

import com.springboot.w3.springboot_w3.Dto.*;
import com.springboot.w3.springboot_w3.Dto.model.*;
import com.springboot.w3.springboot_w3.Jwt.JwtTokenProvider;
import com.springboot.w3.springboot_w3.Repository.CommentLikeRepository;
import com.springboot.w3.springboot_w3.Repository.CommentRepository;
import com.springboot.w3.springboot_w3.Repository.RecommentLikeRepository;
import com.springboot.w3.springboot_w3.Repository.RecommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RecommentService {

    private final RecommentRepository repository;
    private final JwtTokenProvider jwtTokenProvider;
    private final CommentRepository commentRepository;
    private final RecommentLikeRepository recommentLikeRepository;
    private final CommentLikeRepository commentLikeRepository;


    public ResponseEntity<ResponseModel> postRecomment(Long comment_id, CommentRequestDto requestDto, HttpServletRequest request) {

        String token = request.getHeader("Authorization");

        if (token == null || !jwtTokenProvider.validateToken(token)) {
            ResponseModel responseModel = ResponseModel.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .httpStatus(HttpStatus.OK)
                    .message("토큰값이 없거나 유효하지 않습니다.").build();

            return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
        }

        String username = jwtTokenProvider.getUserPk(token);
        Recomment recomment = new Recomment(requestDto, username);
        Comment comment = commentRepository.findById(comment_id);
        recomment.setComment(comment);
        int recommentnum = comment.getRecommentNum() + 1;
        comment.updateRecommentNum(recommentnum);

        repository.save(recomment);

        ResponseModel responseModel = ResponseModel.builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("Post recomment success !").build();

        return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());

    }

    public ResponseEntity<ResponseModel> getAllRecomment(Long comment_id) {
        Comment comment = commentRepository.findById(comment_id);

        if (comment == null){
            ResponseModel responseModel = ResponseModel.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("해당하는 댓글 아이디가 없습니다.").build();

            return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
        }

        List<RecommentLike> recommentLikes = recommentLikeRepository.findAll();

        List<Recomment> recomments = repository.findAll();
        List<RecommentResponseDto> recommentResponseDtos = new ArrayList<>();
        for (int a=0; a<recomments.size(); a++){
            List<LikeDto> likeDtos = new ArrayList<>();
            for(int b=0; b<recommentLikes.size(); b++){

                if(recommentLikes.get(b).getRecomment().getId() ==recomments.get(a).getId()){
                    likeDtos.add(new LikeDto(recommentLikes.get(b)));
                }
            }

            if (comment_id == recomments.get(a).getComment().getId()){
                RecommentResponseDto responseDto = new RecommentResponseDto(recomments.get(a));
                responseDto.setRecommentLikes(likeDtos);
                recommentResponseDtos.add(responseDto);
            }

        }

        CommentResponseDto commentResponseDto = new CommentResponseDto(comment);
        List<CommentLike> commentLikes = commentLikeRepository.findAll();
        List<LikeDto> commentLikeList = new ArrayList<>();

        for (int a=0; a<commentLikes.size(); a++){
            if (commentLikes.get(a).getComment().getId() == comment_id)
                commentLikeList.add(new LikeDto(commentLikes.get(a)));
        }
        commentResponseDto.setComment_like_list(commentLikeList);
        commentResponseDto.setRecomment(recommentResponseDtos);

        List<CommentResponseDto> list = new ArrayList<>();
        list.add(commentResponseDto);

        ResponseModel responseModel = ResponseModel.builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("get recomment success !")
                .data(list).build();

        return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());

    }

    public ResponseEntity<ResponseModel> putRecomment(Long recomment_id, CommentRequestDto requestDto, HttpServletRequest request) {

        String token = request.getHeader("Authorization");

        if (token == null || !jwtTokenProvider.validateToken(token)) {
            ResponseModel responseModel = ResponseModel.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("토큰값이 없거나 유효하지 않습니다.").build();

            return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
        }

        String username = jwtTokenProvider.getUserPk(token);
        Recomment recomment = repository.findById(recomment_id);
        if (!recomment.getName().equals(username)){
            ResponseModel responseModel = ResponseModel.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("해당 대댓글의 수정 권한이 없습니다.").build();

            return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
        }

        recomment.update(requestDto);
        List<Recomment> recomments = new ArrayList<>();
        recomments.add(recomment);

        ResponseModel responseModel = ResponseModel.builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("put recomment success !")
                .data(recomments).build();

        return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
    }

    public ResponseEntity<ResponseModel> deleteRecomment(Long recomment_id, HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token == null || !jwtTokenProvider.validateToken(token)) {
            ResponseModel responseModel = ResponseModel.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("토큰값이 없거나 유효하지 않습니다.").build();

            return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
        }

        String username = jwtTokenProvider.getUserPk(token);
        Recomment recomment = repository.findById(recomment_id);
        if (!recomment.getName().equals(username)){
            ResponseModel responseModel = ResponseModel.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("해당 대댓글의 수정 권한이 없습니다.").build();

            return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
        }

        int recommentnum = recomment.getComment().getRecommentNum() - 1;
        recomment.getComment().updateRecommentNum(recommentnum);

        repository.delete(recomment);
        ResponseModel responseModel = ResponseModel.builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("delete recomment success !").build();

        return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());

    }

    public ResponseEntity<ResponseModel> putCommentLikeTrue(Long recomment_id, HttpServletRequest request) {

        Recomment recomment = repository.findById(recomment_id);
        if (recomment == null){
            ResponseModel responseModel = ResponseModel.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("해당 대댓글이 존재하지 않습니다.").build();

            return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
        }

        String token = request.getHeader("Authorization");
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            ResponseModel responseModel = ResponseModel.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .httpStatus(HttpStatus.OK)
                    .message("토큰값이 없거나 유효하지 않습니다.").build();

            return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
        }

        String username = jwtTokenProvider.getUserPk(token);
        List<RecommentLike> recommentLikes = recommentLikeRepository.findAll();

        for (int a = 0; a < recommentLikes.size(); a++) {
            if (recommentLikes.get(a).getName().equals(username) && recomment_id == recommentLikes.get(a).getRecomment().getId()) {
                ResponseModel responseModel = ResponseModel.builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .httpStatus(HttpStatus.OK)
                        .message("이미 해당 게시글에 좋아요가 되었습니다.").build();

                return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
            }
        }


        RecommentLike recommentLike = new RecommentLike(username, recomment);
        recommentLikeRepository.save(recommentLike);

        int likenum = recomment.getLikeNum() + 1;
        recomment.setLikeNum(likenum);
        repository.save(recomment);

        ResponseModel responseModel = ResponseModel.builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("like success!").build();
        return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());

    }

    public ResponseEntity<ResponseModel> putCommentLikeFalse(Long recomment_id, HttpServletRequest request) {
        Recomment recomment = repository.findById(recomment_id);
        if (recomment == null){
            ResponseModel responseModel = ResponseModel.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("해당 댓글이 존재하지 않습니다.").build();

            return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
        }

        String token = request.getHeader("Authorization");
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            ResponseModel responseModel = ResponseModel.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .httpStatus(HttpStatus.BAD_REQUEST)
                    .message("토큰값이 없거나 유효하지 않습니다.").build();

            return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
        }
        String username = jwtTokenProvider.getUserPk(token);

        List<RecommentLike> recommentLikes = recommentLikeRepository.findAll();
        for(int a=0; a<recommentLikes.size(); a++){
            if (recommentLikes.get(a).getRecomment().getId() == recomment_id && recommentLikes.get(a).getName().equals(username)){

                recommentLikeRepository.delete(recommentLikes.get(a));
                int likenum = recomment.getLikeNum() - 1;
                recomment.setLikeNum(likenum);
                repository.save(recomment);


                ResponseModel responseModel = ResponseModel.builder()
                        .code(HttpStatus.OK.value())
                        .httpStatus(HttpStatus.OK)
                        .message("comment like cancel success!").build();
                return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
            }
        }

        ResponseModel responseModel = ResponseModel.builder()
                .code(HttpStatus.BAD_REQUEST.value())
                .httpStatus(HttpStatus.BAD_REQUEST)
                .message("해당 댓글의 좋아요가 없습니다.").build();
        return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
    }


}
