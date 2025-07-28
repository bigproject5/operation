package aivle.project.operation.service;

import aivle.project.operation.domain.Admin;
import aivle.project.operation.domain.AdminRepository;
import aivle.project.operation.domain.Worker;
import aivle.project.operation.domain.WorkerRepository;
import aivle.project.operation.domain.dto.LoginRequestDto;
import aivle.project.operation.domain.dto.LoginResponseDto;
import aivle.project.operation.domain.dto.SignupRequestDto;
import aivle.project.operation.domain.dto.WorkerSignupRequestDto;
import aivle.project.operation.infra.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final WorkerRepository workerRepository;
    private final JwtUtil jwtUtil;

    @Value("${jwt.expiredMs}")
    private Long expirationTime;

    @Transactional
    public void adminSignup(SignupRequestDto requestDto) {
        Optional<Admin> checkLoginId = adminRepository.findByLoginId(requestDto.getLoginId());
        if (checkLoginId.isPresent()) {
            throw new IllegalArgumentException("This ID already exists.");
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        Admin admin = requestDto.toEntity(encodedPassword);
        adminRepository.save(admin);
    }

    @Transactional
    public void workerSignup(WorkerSignupRequestDto requestDto) {
        Optional<Worker> checkLoginId = workerRepository.findByLoginId(requestDto.getLoginId());
        if (checkLoginId.isPresent()) {
            throw new IllegalArgumentException("This ID already exists.");
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        Worker worker = requestDto.toEntity(encodedPassword);
        workerRepository.save(worker);
    }

    public LoginResponseDto adminLogin(LoginRequestDto requestDto){
        Optional<Admin> checkLoginId = adminRepository.findByLoginId(requestDto.getLoginId());
        if (checkLoginId.isEmpty()) {
            throw new BadCredentialsException("ID or password is not correct");
        }

        Admin admin = checkLoginId.get();
        if(!passwordEncoder.matches(requestDto.getPassword(), admin.getPassword())){
            throw new BadCredentialsException("ID or password is not correct");
        }
        String token = jwtUtil.createToken("ADMIN", admin.getAdminId());
        LoginResponseDto response = new LoginResponseDto();
        response.setToken(token);
        response.setRole("ADMIN");
        response.setExpiresIn(expirationTime / 1000); // 초단위
        return response;
    }

    public LoginResponseDto workerLogin(LoginRequestDto requestDto){
        //회원가입 구현 안되어있음
        Optional<Worker> checkLoginId = workerRepository.findByLoginId(requestDto.getLoginId());
        if (checkLoginId.isEmpty()) {
            throw new BadCredentialsException("ID or password is not correct");
        }

        Worker worker = checkLoginId.get();
        if(!passwordEncoder.matches(requestDto.getPassword(), worker.getPassword())){
            throw new BadCredentialsException("ID or password is not correct");
        }

        String token = jwtUtil.createToken("WORKER", worker.getWorkerId());
        LoginResponseDto response = new LoginResponseDto();
        response.setToken(token);
        response.setRole("WORKER");
        response.setExpiresIn(expirationTime / 1000); // 초
        return response;
    }

    public UserDetails loadUserByAdminId(Long userId) throws UsernameNotFoundException {
        Admin admin = adminRepository.findByAdminId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found: " + userId));

        return User.builder()
                .username(admin.getLoginId())
                .password(admin.getPassword())
                .authorities("ROLE_ADMIN")
                .build();
    }

    public UserDetails loadUserByWorkerId(Long userId) throws UsernameNotFoundException {
        Worker worker = workerRepository.findByWorkerId(userId)
                .orElseThrow(() -> new UsernameNotFoundException("Admin not found: " + userId));

        return User.builder()
                .username(worker.getLoginId())
                .password(worker.getPassword())
                .authorities("ROLE_WORKER")
                .build();
    }

}
