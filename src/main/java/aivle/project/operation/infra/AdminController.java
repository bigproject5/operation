package aivle.project.operation.infra;


import aivle.project.operation.domain.AdminRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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

    public void adminRegist(){

    }

    @GetMapping(value = "/login")
    public void adminLogin(){

    }
}
`