package com.springboot.w3.springboot_w3.Service;


import com.springboot.w3.springboot_w3.Dto.LoginDto;
import com.springboot.w3.springboot_w3.Dto.RefreshTokenDto;
import com.springboot.w3.springboot_w3.Dto.SignupRequestDto;
import com.springboot.w3.springboot_w3.Jwt.JwtTokenProvider;
import com.springboot.w3.springboot_w3.Repository.RefreshTokenRepository;
import com.springboot.w3.springboot_w3.Repository.UserRepository;
import com.springboot.w3.springboot_w3.Dto.ResponseModel;
import com.springboot.w3.springboot_w3.Dto.model.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AuthUserService implements UserDetailsService {

    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider tokenProvider;



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return (UserDetails) userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

    public void registerUser(SignupRequestDto requestDto) {
        /* 회원 ID 중복 확인 */
        String username = requestDto.getUsername();
        String pw = requestDto.getPassword();
        String pw_confirm = requestDto.getPasswordConfirm();


        Optional<Users> found = userRepository.findByUsername(username);
        if (found.isPresent()) {
            throw new IllegalArgumentException("중복된 사용자 ID 가 존재합니다.");
        }

        if (!pw.equals(pw_confirm)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        // 패스워드 암호화
        String password = bCryptPasswordEncoder.encode(requestDto.getPassword());


        Users users = new Users();
        users.setUsername(username);
        users.setPassword(password);

        userRepository.save(users);
    }

    public Users getuser(String username){
        Users users = userRepository.findUsersByUsername(username);
        return users;
    }

    public void login(String username, String password){

        Optional<Users> found = userRepository.findByUsername(username);
        if (!found.isPresent()) {
            throw new IllegalArgumentException("ID가 존재하지 않습니다.");
        }

        String u_password = found.get().getPassword();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String pp = encoder.encode(password);
        if (!encoder.matches(password, u_password)){
            System.out.println(u_password);
            System.out.println(pp);
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");

        }
    }


    //로그인 시, 토큰을 헤더에 담아주기
    //저장되어있던 refresh 토큰도 update 해주기
    public ResponseEntity<ResponseModel> tokenPostHeader(String accressToken, String refreshToken, HttpServletResponse response, LoginDto loginDto) {
        String username = loginDto.getUsername();
        Users user = userRepository.findUsersByUsername(username);
        Long user_id = user.getId();

        Optional<RefreshTokenDto> refreshTokenDto = refreshTokenRepository.findById(user_id);

        if (refreshTokenDto.isPresent())
            refreshTokenDto.get().update(refreshToken);
        else
            refreshTokenRepository.save(new RefreshTokenDto(user_id, refreshToken));

        response.addHeader("Access-Token", accressToken);
        response.addHeader("Refresh-Token", refreshToken);

        ResponseModel responseModel = ResponseModel.builder()
                .code(HttpStatus.OK.value())
                .httpStatus(HttpStatus.OK)
                .message("로그인 완료").build();
        return new ResponseEntity<>(responseModel, responseModel.getHttpStatus());
    }


}