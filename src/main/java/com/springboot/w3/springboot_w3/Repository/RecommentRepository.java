package com.springboot.w3.springboot_w3.Repository;

import com.springboot.w3.springboot_w3.Dto.model.Comment;
import com.springboot.w3.springboot_w3.Dto.model.Recomment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommentRepository extends JpaRepository<Recomment, Integer> {

    Recomment findById(Long recomment_id);
    List<Recomment> findRecommentByComment(Comment comment);
}