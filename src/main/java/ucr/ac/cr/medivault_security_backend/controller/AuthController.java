package ucr.ac.cr.medivault_security_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import ucr.ac.cr.medivault_security_backend.business.AuthService;
import ucr.ac.cr.medivault_security_backend.business.SecurityAuditorService;
import ucr.ac.cr.medivault_security_backend.controller.dto.LoginRequestDto;
import ucr.ac.cr.medivault_security_backend.controller.dto.LoginResponseDto;
import ucr.ac.cr.medivault_security_backend.security.ClientIpResolver;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final SecurityAuditorService auditor;

    public AuthController(AuthService authService, SecurityAuditorService auditor) {
        this.authService = authService;
        this.auditor = auditor;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto request,
                                                  HttpServletRequest httpRequest) {
        try {
            return ResponseEntity.ok(authService.authenticate(request));
        } catch (BadCredentialsException ex) {
            auditor.logLoginFailed(
                    request.username(),
                    ClientIpResolver.resolve(httpRequest),
                    httpRequest.getRequestURI(),
                    ex.getMessage());
            throw ex;
        }
    }
}