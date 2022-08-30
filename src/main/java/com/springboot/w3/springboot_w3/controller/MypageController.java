package com.springboot.w3.springboot_w3.controller;

import com.springboot.w3.springboot_w3.Dto.ResponseModel;
import com.springboot.w3.springboot_w3.Service.MypageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
public class MypageController {

    private final MypageService mypageService;

    @GetMapping("/api/auth/mypage")
    public ResponseEntity<ResponseModel> getMypage(HttpServletRequest httpServletRequest){
        return mypageService.getMypage(httpServletRequest);
    }


}
