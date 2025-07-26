package aivle.project.operation.infra;

import aivle.project.operation.domain.dto.LoginRequestDto;
import aivle.project.operation.domain.dto.LoginResponseDto;
import aivle.project.operation.domain.dto.SignupRequestDto;
import aivle.project.operation.domain.dto.SignupResponseDto;
import aivle.project.operation.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/api/operation/admin")
@RestController
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
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
}
