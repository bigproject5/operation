package aivle.project.operation.service;

import aivle.project.operation.domain.Admin;
import aivle.project.operation.domain.AdminRepository;
import aivle.project.operation.domain.dto.LoginRequestDto;
import aivle.project.operation.domain.dto.LoginResponseDto;
import aivle.project.operation.domain.dto.SignupRequestDto;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void signup(SignupRequestDto requestDto) {
        Optional<Admin> checkLoginId = adminRepository.findByLoginId(requestDto.getLoginId());
        if (checkLoginId.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        Admin admin = requestDto.toEntity(encodedPassword);
        adminRepository.save(admin);
    }

    public LoginResponseDto adminLogin(LoginRequestDto requestDto){
        Optional<Admin> checkLoginId = adminRepository.findByLoginId(requestDto.getLoginId());
        if (checkLoginId.isEmpty()) {
            throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다");
        }

        Admin admin = checkLoginId.get();
        if(!passwordEncoder.matches(requestDto.getPassword(), admin.getPassword())){
            throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다");
        }

        LoginResponseDto response = new LoginResponseDto();

        return response;
    }

    public LoginResponseDto workerLogin(LoginRequestDto requestDto){
        Optional<Admin> checkLoginId = adminRepository.findByLoginId(requestDto.getLoginId());
        if (checkLoginId.isEmpty()) {
            throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다");
        }

        Admin admin = checkLoginId.get();
        if(!passwordEncoder.matches(requestDto.getPassword(), admin.getPassword())){
            throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다");
        }

        LoginResponseDto response = new LoginResponseDto();

        return response;
    }
}
