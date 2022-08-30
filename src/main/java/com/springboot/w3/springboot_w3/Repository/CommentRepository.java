package com.springboot.w3.springboot_w3.Repository;


import com.springboot.w3.springboot_w3.Dto.model.Comment;
import com.springboot.w3.springboot_w3.Dto.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

    Comment findById(Long comment_id);
    List<Comment> findCommentByPost(Post post);
}