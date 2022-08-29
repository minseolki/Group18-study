package com.springboot.w3.springboot_w3.Repository;

import com.springboot.w3.springboot_w3.Dto.model.Recomment;
import com.springboot.w3.springboot_w3.Dto.model.RecommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommentLikeRepository extends JpaRepository<RecommentLike, Long> {
}