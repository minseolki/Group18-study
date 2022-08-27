package com.springboot.w3.springboot_w3.Repository;

import com.springboot.w3.springboot_w3.Dto.RefreshTokenDto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenDto, Long> {

    boolean existsByRefreshToken(String token);
}