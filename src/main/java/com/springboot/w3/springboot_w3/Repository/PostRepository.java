package com.springboot.w3.springboot_w3.Repository;

import com.springboot.w3.springboot_w3.Dto.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

}