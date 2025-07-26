package aivle.project.operation.infra;


import aivle.project.operation.domain.AdminRepository;
import aivle.project.operation.domain.SignupResponse;
import aivle.project.operation.domain.dto.SignupRequestDto;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(value = "/api/operation/admin")
@RestController
@Transactional
public class AdminController {

    private final AdminRepository adminRepository;

    @Autowired
    public AdminController(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @PostMapping
    public ResponseEntity<SignupResponse> adminRegister(SignupRequest signupRequest){
        return
    }

    @PostMapping(value = "/login")
    public void adminLogin(){

    }
}
