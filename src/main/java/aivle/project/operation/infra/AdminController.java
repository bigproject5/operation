package aivle.project.operation.infra;

import aivle.project.operation.domain.dto.*;
import aivle.project.operation.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api/operation/admin")
@RestController
public class AdminController {

    private final AdminService adminService;
    private final JwtUtil jwtUtil;

    @Autowired
    public AdminController(AdminService adminService, JwtUtil jwtUtil) {
        this.adminService = adminService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<SignupResponseDto> adminRegister(@RequestBody SignupRequestDto signupRequestDto){
        adminService.signup(signupRequestDto);
        return ResponseEntity.ok(new SignupResponseDto(signupRequestDto));
    }

    @PostMapping(value = "/login")
    public ResponseEntity<LoginResponseDto> adminLogin(@RequestBody LoginRequestDto loginRequestDto){
        LoginResponseDto response = adminService.adminLogin(loginRequestDto);
        return ResponseEntity.ok(response);
    }

    //for token test api
    @PostMapping(value = "/test")
    public ResponseEntity<ResponseDto> tokenTest(
            @RequestHeader("Authorization") String token,
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
            response.setMessage("Token is valid");
            response.setRequest(requestDto.getMessage());
            response.setId(jwtUtil.getUserId(jwt));
            response.setRole(jwtUtil.getUserRole(jwt));
        }
        return ResponseEntity.ok(response);
    }

}
