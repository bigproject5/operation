package aivle.project.operation.infra.controller;

import aivle.project.operation.domain.dto.*;
import aivle.project.operation.infra.exception.CaptchaFailedException;
import aivle.project.operation.infra.security.JwtUtil;
import aivle.project.operation.service.LoginService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@RequestMapping(value = "/api/operation")
@RestController
public class LoginController {

    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @PostMapping(value = "/signup/admin")
    public ResponseEntity<?> adminRegister(@RequestBody @Valid AdminSignupRequestDto adminSignupRequestDto){
        try{
            SignupResponseDto responseDto =  loginService.adminSignup(adminSignupRequestDto);
            return ResponseEntity.ok(responseDto);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (CaptchaFailedException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error");
        }
    }

    @PostMapping(value = "/signup/workers")
    public ResponseEntity<SignupResponseDto> workerRegister(@RequestBody @Valid WorkerSignupRequestDto workerSignupRequestDto){
        loginService.workerSignup(workerSignupRequestDto);
        return ResponseEntity.ok(new SignupResponseDto(workerSignupRequestDto));
    }

    @PostMapping(value = "/login/admin")
    public ResponseEntity<LoginResponseDto> adminLogin(@RequestBody LoginRequestDto loginRequestDto){
        LoginResponseDto response = loginService.adminLogin(loginRequestDto);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping(value = "/login/workers")
    public ResponseEntity<LoginResponseDto> workerLogin(@RequestBody LoginRequestDto loginRequestDto){
        LoginResponseDto response = loginService.workerLogin(loginRequestDto);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/admin/check-id")
    public ResponseEntity<AdminLoginIdCheckResponseDto>checkIdAvailability (@RequestBody AdminLoginIdCheckRequestDto loginId){
        AdminLoginIdCheckResponseDto response = loginService.CheckAdminLoginIdAvailable(loginId);
        return ResponseEntity.ok(response);
    }

    /**
     * TODO: 개발용 함수, 추후 제거 필요
     */
    @PostMapping("/login/dev")
    public ResponseEntity<LoginResponseDto> DevLogin(){
        LoginResponseDto response = loginService.devLogin();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Name") String encodedName,
            @RequestHeader(value = "X-User-Task-Type", required = false) String taskType
    ){
        String name = URLDecoder.decode(encodedName, StandardCharsets.UTF_8);
        String maskedName = loginService.maskName(name);
        UserDto user = new UserDto();
        user.setId(Long.parseLong(userId));
        user.setRole(role);
        user.setName(maskedName);
        user.setTaskType(taskType);
        return ResponseEntity.ok(user);
    }


    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Notice Service is running!");
    }
}
