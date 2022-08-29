package com.springboot.w3.springboot_w3.Service;

import com.springboot.w3.springboot_w3.Dto.CommentRequestDto;
import com.springboot.w3.springboot_w3.Dto.CommentResponseDto;
import com.springboot.w3.springboot_w3.Dto.RecommentResponseDto;
import com.springboot.w3.springboot_w3.Dto.ResponseModel;
import com.springboot.w3.springboot_w3.Dto.model.Comment;
import com.springboot.w3.springboot_w3.Dto.model.Recomment;
import com.springboot.w3.springboot_w3.Jwt.JwtTokenProvider;
import com.springboot.w3.springboot_w3.Repository.CommentRepository;
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

        List<Recomment> recomments = repository.findAll();
        List<RecommentResponseDto> recommentResponseDtos = new ArrayList<>();
        for (int a=0; a<recomments.size(); a++){
            if (comment_id == recomments.get(a).getComment().getId())
                recommentResponseDtos.add(new RecommentResponseDto(recomments.get(a)));
        }

        CommentResponseDto commentResponseDto = new CommentResponseDto(comment);
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

        repository.delete(recomment);
        ResponseModel responseModel = ResponseModel.builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("delete recomment success !").build();

        return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());

    }
}
