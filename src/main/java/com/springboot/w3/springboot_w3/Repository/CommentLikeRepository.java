package com.springboot.w3.springboot_w3.Repository;

import com.springboot.w3.springboot_w3.Dto.model.Comment;
import com.springboot.w3.springboot_w3.Dto.model.CommentLike;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentLikeRepository  extends JpaRepository<CommentLike, Long> {
    List<CommentLike> findCommentLikeByComment(Comment comment);
}
