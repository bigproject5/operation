package aivle.project.operation.infra.controller;

import aivle.project.operation.domain.dto.*;
import aivle.project.operation.infra.security.JwtUtil;
import aivle.project.operation.service.LoginService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/operation")
@RestController
public class LoginController {

    private final LoginService loginService;
    private final JwtUtil jwtUtil;

    @Autowired
    public LoginController(LoginService loginService, JwtUtil jwtUtil) {
        this.loginService = loginService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping(value = "/admin/signup")
    public ResponseEntity<SignupResponseDto> adminRegister(@RequestBody @Valid AdminSignupRequestDto adminSignupRequestDto){
        loginService.adminSignup(adminSignupRequestDto);
        return ResponseEntity.ok(new SignupResponseDto(adminSignupRequestDto));
    }

    @PostMapping(value = "/workers/signup")
    public ResponseEntity<SignupResponseDto> workerRegister(@RequestBody @Valid WorkerSignupRequestDto workerSignupRequestDto){
        loginService.workerSignup(workerSignupRequestDto);
        return ResponseEntity.ok(new SignupResponseDto(workerSignupRequestDto));
    }

    @PostMapping(value = "/admin/login")
    public ResponseEntity<LoginResponseDto> adminLogin(@RequestBody LoginRequestDto loginRequestDto){
        LoginResponseDto response = loginService.adminLogin(loginRequestDto);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping(value = "/workers/login")
    public ResponseEntity<LoginResponseDto> workerLogin(@RequestBody LoginRequestDto loginRequestDto){
        LoginResponseDto response = loginService.workerLogin(loginRequestDto);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @RequestHeader("X-User-Name") String name,
            @RequestHeader("X-User-Task-Type") String taskType
    ){
        UserDto user = new  UserDto();
        user.setId(Long.parseLong(userId));
        user.setRole(role);
        user.setName(name);
        user.setTaskType(taskType);
        return  ResponseEntity.ok(user);
    }
    //인가 테스트 - admin
    @PostMapping(value = "/admin/test")
    public ResponseEntity<ResponseDto> tokenTest(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @RequestBody RequestDto requestDto
    ){

        String jwt = token.substring(7);
        ResponseDto response = new ResponseDto();
        if(jwtUtil.isExpired(jwt)){
            response.setCode(403);
            response.setRequest(requestDto.getMessage());
            response.setMessage("Token is not valid");
        }
        else{
            response.setCode(200);
            response.setMessage("Token is valid: " + role + userId);
            response.setRequest(requestDto.getMessage());
            response.setId(Long.parseLong(userId));
            response.setRole(role);
        }
        return ResponseEntity.ok(response);
    }
    //인가 테스트 - worker
    @PostMapping(value = "/workers/test")
    public ResponseEntity<ResponseDto> workerTokenTest(
            @RequestHeader("Authorization") String token,
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-User-Role") String role,
            @RequestBody RequestDto requestDto
    ){

        String jwt = token.substring(7);
        ResponseDto response = new ResponseDto();
        if(jwtUtil.isExpired(jwt)){
            response.setCode(403);
            response.setRequest(requestDto.getMessage());
            response.setMessage("Token is not valid");
        }
        else{
            response.setCode(200);
            response.setMessage("Token is valid: " + role + userId);
            response.setRequest(requestDto.getMessage());
            response.setId(Long.parseLong(userId));
            response.setRole(role);
        }
        return ResponseEntity.ok(response);
    }


}
