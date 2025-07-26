package aivle.project.operation.service;

import aivle.project.operation.domain.Admin;
import aivle.project.operation.domain.AdminRepository;
import aivle.project.operation.domain.Worker;
import aivle.project.operation.domain.WorkerRepository;
import aivle.project.operation.domain.dto.LoginRequestDto;
import aivle.project.operation.domain.dto.LoginResponseDto;
import aivle.project.operation.domain.dto.SignupRequestDto;
import aivle.project.operation.infra.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final WorkerRepository workerRepository;
    private final JwtUtil jwtUtil;

    @Value("${jwt.expiredMs}")
    private Long expirationTime;

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
        String token = jwtUtil.createToken("ADMIN", admin.getAdminId());
        LoginResponseDto response = new LoginResponseDto();
        response.setToken(token);
        response.setRole("ADMIN");
        response.setExpiresIn(expirationTime / 1000); // 초단위
        return response;
    }

    /**
     * @TODO worker service 구현시 이동
     * @param requestDto LoginRequestDto 아이디, 비밀번호
     * @return response LoginResponseDto 토큰
     */
    //Worker service 로 이동
    public LoginResponseDto workerLogin(LoginRequestDto requestDto){
        Optional<Worker> checkLoginId = workerRepository.findByLoginId(requestDto.getLoginId());
        if (checkLoginId.isEmpty()) {
            throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다");
        }

        Worker worker = checkLoginId.get();
        if(!passwordEncoder.matches(requestDto.getPassword(), worker.getPassword())){
            throw new BadCredentialsException("아이디 또는 비밀번호가 올바르지 않습니다");
        }

        String token = jwtUtil.createToken("WORKER", worker.getWorkerId());
        LoginResponseDto response = new LoginResponseDto();
        response.setToken(token);
        response.setRole("WORKER");
        response.setExpiresIn(expirationTime / 1000); // 초
        return response;
    }
}
