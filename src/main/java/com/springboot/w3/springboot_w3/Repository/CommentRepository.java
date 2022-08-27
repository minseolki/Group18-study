package com.springboot.w3.springboot_w3.Repository;


import com.springboot.w3.springboot_w3.Dto.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Integer> {

}