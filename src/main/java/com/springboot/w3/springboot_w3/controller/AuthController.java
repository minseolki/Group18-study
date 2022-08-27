package com.springboot.w3.springboot_w3.controller;

import com.springboot.w3.springboot_w3.Dto.LoginDto;
import com.springboot.w3.springboot_w3.Jwt.JwtTokenProvider;
import com.springboot.w3.springboot_w3.Dto.SignupRequestDto;
import com.springboot.w3.springboot_w3.Service.AuthUserService;
import com.springboot.w3.springboot_w3.Dto.ResponseModel;
import com.springboot.w3.springboot_w3.Dto.model.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RestController
public class AuthController {
    @Autowired
    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    private final AuthUserService authUserService;


    // 회원가입
    @PostMapping("/api/member/join")
    public void join(@RequestBody SignupRequestDto user) {
        authUserService.registerUser(user);
    }

    // 로그인
    @PostMapping("/api/member/login")
    public ResponseEntity<ResponseModel> login(@RequestBody LoginDto loginDto, HttpServletResponse response) {

        authUserService.login(loginDto.getUsername(), loginDto.getPassword());

        String accressToken = jwtTokenProvider.createToken(loginDto.getUsername());
        String refreshToken = jwtTokenProvider.createRefreshToken(loginDto.getUsername());

        return authUserService.tokenPostHeader(accressToken, refreshToken, response, loginDto);
    }


    // 로그인 됐는지 확인
    @GetMapping("/user")
    public Users detail(HttpServletRequest request){
        String token = request.getHeader("Authorization");
        String username = jwtTokenProvider.getUserPk(token);

        return authUserService.getuser(username);
    }
}

