package ucr.ac.cr.medivault_security_backend.business;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ucr.ac.cr.medivault_security_backend.controller.dto.LoginRequestDto;
import ucr.ac.cr.medivault_security_backend.controller.dto.LoginResponseDto;
import ucr.ac.cr.medivault_security_backend.data.UserRepository;
import ucr.ac.cr.medivault_security_backend.domain.User;
import ucr.ac.cr.medivault_security_backend.security.JwtTokenProvider;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final JwtTokenProvider jwtProvider;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder encoder,
                       JwtTokenProvider jwtProvider) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtProvider = jwtProvider;
    }

    @Transactional(readOnly = true)
    public LoginResponseDto authenticate(LoginRequestDto request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials."));

        if (!encoder.matches(request.password(), user.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials.");
        }

        if (!"ACTIVO".equals(user.getStatus())) {
            throw new BadCredentialsException("Inactive or locked account.");
        }

        String token = jwtProvider.generateToken(user.getUsername(), user.getRole().name());

        return new LoginResponseDto(
                token,
                "Bearer",
                jwtProvider.getExpirationMs(),
                user.getUsername(),
                user.getRole().name()
        );
    }
}