package com.springboot.w3.springboot_w3.Service;

import com.springboot.w3.springboot_w3.Dto.CommentRequestDto;
import com.springboot.w3.springboot_w3.Dto.CommentResponseDto;
import com.springboot.w3.springboot_w3.Jwt.JwtTokenProvider;
import com.springboot.w3.springboot_w3.Repository.CommentRepository;
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
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {

    public final CommentRepository repository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PostRepository postRepository;

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

        List<Comment> comments = repository.findAll();
        List<Comment> id_comment = new ArrayList<>();

        for(int a=0; a<comments.size(); a++){
            if (comments.get(a).getPost().getId() == intid)
                id_comment.add(comments.get(a));
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

}
