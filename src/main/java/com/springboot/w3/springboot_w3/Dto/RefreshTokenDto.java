package com.springboot.w3.springboot_w3.Dto;

import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@NoArgsConstructor
public class RefreshTokenDto {

    @Id
    @Column(nullable = false)
    private Long user_id;

    @Column(nullable = false)
    private String refreshToken;

    public RefreshTokenDto(Long user_id, String refreshToken){
        this.user_id = user_id;
        this.refreshToken = refreshToken;
    }

    public void update(String refreshToken){
        this.refreshToken = refreshToken;
    }
}