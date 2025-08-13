package aivle.project.operation.service;

import aivle.project.operation.domain.Admin;
import aivle.project.operation.domain.AdminRepository;
import aivle.project.operation.domain.Worker;
import aivle.project.operation.domain.WorkerRepository;
import aivle.project.operation.domain.dto.*;
import aivle.project.operation.infra.exception.CaptchaFailedException;
import aivle.project.operation.infra.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

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

    @Value("${reCaptcha.secretKey}")
    private String reCaptchaSecret;

    @Transactional
    public SignupResponseDto adminSignup(AdminSignupRequestDto requestDto) {
        Optional<Admin> checkLoginId = adminRepository.findByLoginId(requestDto.getLoginId());
        if (checkLoginId.isPresent()) {
            throw new IllegalArgumentException("This ID already exists.");
        }

        String verifyUrl = "https://www.google.com/recaptcha/api/siteverify";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", reCaptchaSecret); // 서버 전용
        params.add("response", requestDto.getReCaptchaToken());

        RestTemplate restTemplate = new RestTemplate();
        RecaptchaResponse recaptchaResponse =
                restTemplate.postForObject(verifyUrl, params, RecaptchaResponse.class);

        assert recaptchaResponse != null;
        if (!recaptchaResponse.isSuccess() || recaptchaResponse.getScore() < 0.5) {
            throw new CaptchaFailedException("Captcha failed");
        }

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        Admin admin = requestDto.toEntity(encodedPassword);
        adminRepository.save(admin);
        return new SignupResponseDto(requestDto);
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
        String token = jwtUtil.createToken("ADMIN", admin.getAdminId(), admin.getName());
        LoginResponseDto response = new LoginResponseDto();
        response.setToken(token);
        response.setExpiresIn(expirationTime / 1000); // 초단위
        UserDto user = new UserDto();
        user.setId(admin.getAdminId());
        user.setName(admin.getName());
        user.setRole("ADMIN");
        response.setUser(user);

        return response;
    }

    public LoginResponseDto workerLogin(LoginRequestDto requestDto){
        Optional<Worker> checkLoginId = workerRepository.findByLoginId(requestDto.getLoginId());
        if (checkLoginId.isEmpty()) {
            throw new BadCredentialsException("ID or password is not correct");
        }

        Worker worker = checkLoginId.get();
        if(!passwordEncoder.matches(requestDto.getPassword(), worker.getPassword())){
            throw new BadCredentialsException("ID or password is not correct");
        }

        String token = jwtUtil.createToken("WORKER", worker.getWorkerId(), worker.getName());
        LoginResponseDto response = new LoginResponseDto();
        response.setToken(token);
        response.setExpiresIn(expirationTime / 1000); // 초
        UserDto user = new UserDto();
        user.setId(worker.getWorkerId());
        user.setName(worker.getName());
        user.setRole("WORKER");
        response.setUser(user);

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
