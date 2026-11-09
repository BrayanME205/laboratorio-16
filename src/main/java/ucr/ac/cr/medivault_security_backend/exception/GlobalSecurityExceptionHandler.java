package ucr.ac.cr.medivault_security_backend.exception;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import jakarta.servlet.http.HttpServletRequest;
import ucr.ac.cr.medivault_security_backend.security.ClientIpResolver;
import ucr.ac.cr.medivault_security_backend.security.SecurityAuditorService;

@RestControllerAdvice
public class GlobalSecurityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalSecurityExceptionHandler.class);

    private final SecurityAuditorService auditor;

    public GlobalSecurityExceptionHandler(SecurityAuditorService auditor) {
        this.auditor = auditor;
    }

    @ExceptionHandler(AccessDeniedSecurityException.class)
    public ProblemDetail handleAccessDenied(AccessDeniedSecurityException ex,
                                            HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : null;

        auditor.logIdorAttempt(
                username,
                ClientIpResolver.resolve(request),
                request.getRequestURI(),
                ex.getMessage());

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN, ex.getMessage());
        problem.setTitle("Forbidden (IDOR / BOLA)");
        problem.setType(URI.create("https://medivault.ucr.ac.cr/forbidden"));
        return problem;
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ProblemDetail handleFrameworkAccessDenied(AccessDeniedException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.FORBIDDEN, "Access denied.");
        problem.setTitle("Forbidden");
        problem.setType(URI.create("https://medivault.ucr.ac.cr/forbidden"));
        return problem;
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ProblemDetail handleNotFound(ResourceNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, ex.getMessage());
        problem.setTitle("Resource Not Found");
        problem.setType(URI.create("https://medivault.ucr.ac.cr/not-found"));
        return problem;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ProblemDetail handleBadCredentials(BadCredentialsException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED, "Invalid credentials.");
        problem.setTitle("Authentication Failure");
        problem.setType(URI.create("https://medivault.ucr.ac.cr/unauthorized"));
        return problem;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleValidation(MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Data validation failed.");
        problem.setTitle("Validation Error");
        problem.setType(URI.create("https://medivault.ucr.ac.cr/bad-request"));

        Map<String, String> errors = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        problem.setProperty("invalid_fields", errors);
        return problem;
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ProblemDetail handleMalformedRequest(Exception ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Malformed request.");
        problem.setTitle("Bad Request");
        problem.setType(URI.create("https://medivault.ucr.ac.cr/bad-request"));
        return problem;
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail handleGeneric(Exception ex) {
        log.error("Unhandled exception", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error.");
        problem.setTitle("Internal Error");
        problem.setType(URI.create("https://medivault.ucr.ac.cr/internal"));
        return problem;
    }
}
