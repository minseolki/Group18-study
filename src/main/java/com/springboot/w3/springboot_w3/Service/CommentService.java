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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    public final CommentRepository repository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PostRepository postRepository;
    private final CommentLikeRepository commentLikeRepository;
    private final RecommentLikeRepository recommentLikeRepository;
    private final RecommentRepository recommentRepository;

    public List<Comment> getComment(int id){
        List<Comment> comments = repository.findAll();
        List<Comment> id_comment = new ArrayList<>();

        for(int a=0; a<comments.size(); a++){
            if (comments.get(a).getPost().getId() == id)
                id_comment.add(comments.get(a));
        }
        return id_comment;
    }

    public ResponseEntity<ResponseModel> getAllComment(Long id){
        int intid = id.intValue();
        Optional<Post> post = postRepository.findById(id);
        List<Comment> comments = repository.findAll();
        List<CommentResponseDto> id_comment = new ArrayList<>();
        List<CommentLike> commentLikes = commentLikeRepository.findAll();
        List<RecommentLike> recommentLikes = recommentLikeRepository.findAll();

        List<Recomment> recomment = recommentRepository.findAll();
        List<Comment> comments1 = repository.findCommentByPost(post.get());

        for(int a=0; a<comments.size(); a++){
            if (comments.get(a).getPost().getId() == intid){

                //각 댓글에 해당하는 좋아요 유저를 담기
                List<LikeDto> commentLikeDto = new ArrayList<>();
                for(int b=0; b<commentLikes.size(); b++){
                    if (commentLikes.get(b).getComment().getId() == comments.get(a).getId()){
                        commentLikeDto.add(new LikeDto(commentLikes.get(b)));
                    }
                }

                //각 댓글에 해당하는 대댓글
                List<RecommentResponseDto> recommentResponseDtos = new ArrayList<>();

                for (int c=0; c<recomment.size(); c++){
                    if (recomment.get(c).getComment().getId() == comments.get(a).getId()){
                        RecommentResponseDto responseDto = new RecommentResponseDto(recomment.get(c));
                        List<LikeDto> recommentDto = new ArrayList<>();
                        //대댓글에 해당하는 좋아요 유저
                        for (int d=0; d<recommentLikes.size(); d++){
                            if(recomment.get(c).getId() == recommentLikes.get(d).getRecomment().getId()){
                                LikeDto likeDto = new LikeDto(recommentLikes.get(d));
                                recommentDto.add(likeDto);
                            }
                        }
                        responseDto.setLikeNum(recommentDto.size());
                        //responseDto.setRecommentLikes(recommentDto);
                        recommentResponseDtos.add(responseDto);
                    }
                }

                CommentResponseDto commentResponseDto = new CommentResponseDto(comments.get(a));
                commentResponseDto.setLikeNum(commentLikeDto.size());
                //commentResponseDto.setComment_like_list(commentLikeDto);
                commentResponseDto.setRecomment(recommentResponseDtos);
                id_comment.add(commentResponseDto);
            }

        }

        ResponseModel responseModel = ResponseModel.builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("해당 게시글 댓글 조회 성공")
                .data(new ArrayList<>(id_comment)).build();

        return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
    }

    public ResponseEntity<ResponseModel> postCommentService(HttpServletRequest request, CommentRequestDto requestDto, Long post_id){
        String token = request.getHeader("Authorization");

        if (token == null || !jwtTokenProvider.validateToken(token)){
            ResponseModel responseModel = ResponseModel.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .httpStatus(HttpStatus.OK)
                    .message("토큰값이 없거나 유효하지 않습니다. 재로그인 하세요.").build();

            return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
        }

        String username = jwtTokenProvider.getUserPk(token);
        Optional<Post> post = postRepository.findById(post_id);
        Comment comment = new Comment(requestDto,username);
        comment.setPost(post.get());
        repository.save(comment);
        CommentResponseDto commentResponseDto = new CommentResponseDto(comment);

        ArrayList<CommentResponseDto> c = new ArrayList<>();
        c.add(commentResponseDto);

        ResponseModel responseModel = ResponseModel.builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("댓글 작성 완료")
                .data(new ArrayList<>(c)).build();

        return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
    }

    public ResponseEntity<ResponseModel> DeleteCommentService(HttpServletRequest request, Long id){
        String token = request.getHeader("Authorization");
        if (token == null || !jwtTokenProvider.validateToken(token)){
            ResponseModel responseModel = ResponseModel.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .httpStatus(HttpStatus.OK)
                    .message("토큰값이 없거나 유효하지 않습니다. 재로그인 하세요.").build();

            return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
        }

        String username = jwtTokenProvider.getUserPk(token);
        int intid = id.intValue();

        Comment comment = repository.findById(intid).orElseThrow(
                () -> new IllegalArgumentException("해당하는 댓글 아이디가 없습니다")
        );

        if (comment.getName().equals(username)){
            repository.deleteById(intid);
        }

        ResponseModel responseModel = ResponseModel.builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("댓글 삭제 완료").build();
        return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
    }

    public ResponseEntity<ResponseModel> putCommentService(HttpServletRequest request,
                                                           CommentRequestDto requestDto, Long id) {

        String token = request.getHeader("Authorization");
        if (token == null || !jwtTokenProvider.validateToken(token)){
            ResponseModel responseModel = ResponseModel.builder()
                    .code(HttpStatus.BAD_REQUEST.value())
                    .httpStatus(HttpStatus.OK)
                    .message("토큰값이 없거나 유효하지 않습니다. 재로그인 하세요.").build();

            return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
        }

        String username = jwtTokenProvider.getUserPk(token);

        Comment comment = repository.findById(id.intValue()).orElseThrow(
                () -> new IllegalArgumentException("해당하는 댓글 아이디가 없습니다")
        );

        if (comment.getName().equals(username)){
            comment.update(requestDto);
        }

        ArrayList<Comment> c = new ArrayList<>();
        c.add(comment);

        ResponseModel responseModel = ResponseModel.builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("댓글 수정 완료")
                .data(new ArrayList<>(c)).build();

        return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
    }

    public ResponseEntity<ResponseModel> putCommentLikeTrue(Long comment_id, HttpServletRequest request) {

        Comment comment = repository.findById(comment_id);
        if (comment == null){
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

        List<CommentLike> commentLikes = commentLikeRepository.findAll();
        for(int a=0; a<commentLikes.size(); a++){
            if (commentLikes.get(a).getComment().getId() == comment_id && commentLikes.get(a).getName().equals(username)){
                ResponseModel responseModel = ResponseModel.builder()
                        .code(HttpStatus.BAD_REQUEST.value())
                        .httpStatus(HttpStatus.BAD_REQUEST)
                        .message("이미 해당 댓글에 좋아요가 되어 있습니다.").build();
                return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
            }
        }

        CommentLike commentLike = new CommentLike(username, comment);
        commentLikeRepository.save(commentLike);

        int likenum = comment.getLikeNum() + 1;
        comment.setLikeNum(likenum);
        repository.save(comment);

        ResponseModel responseModel = ResponseModel.builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("comment like success !").build();
        return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());

    }

    public ResponseEntity<ResponseModel> putCommentLikeFalse(Long comment_id, HttpServletRequest request) {
        Comment comment = repository.findById(comment_id);
        if (comment == null){
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

        List<CommentLike> commentLikes = commentLikeRepository.findAll();
        for(int a=0; a<commentLikes.size(); a++){
            if (commentLikes.get(a).getComment().getId() == comment_id && commentLikes.get(a).getName().equals(username)){

                commentLikeRepository.delete(commentLikes.get(a));
                int likenum = comment.getLikeNum() - 1;
                comment.setLikeNum(likenum);
                repository.save(comment);

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
