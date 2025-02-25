package study.blogapi.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import study.blogapi.exception.AppException;
import study.blogapi.exception.BlogapiException;
import study.blogapi.model.role.Role;
import study.blogapi.model.role.RoleName;
import study.blogapi.model.user.User;
import study.blogapi.payload.ApiResponse;
import study.blogapi.payload.JwtAuthenticationResponse;
import study.blogapi.payload.LoginRequest;
import study.blogapi.payload.SignUpRequest;
import study.blogapi.repository.RoleRepository;
import study.blogapi.repository.UserRepository;
import study.blogapi.security.JwtTokenProvider;
import study.blogapi.service.UserService;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final String USER_ROLE_NOT_SET = "User role not set";


    private final AuthenticationManager authenticationManager;


    private final UserRepository userRepository;


    private final RoleRepository roleRepository;


    private  final PasswordEncoder passwordEncoder;


    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }


    @PostMapping("signup")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
        if (Boolean.TRUE.equals(userRepository.existsByUsername(signUpRequest.getUsername()))) {
            throw new BlogapiException(HttpStatus.BAD_REQUEST,"Username is already in use");
        }
        if(Boolean.TRUE.equals(userRepository.existsByEmail(signUpRequest.getEmail()))) {
            throw new BlogapiException(HttpStatus.BAD_REQUEST,"Email is already in use");
        }
        String firstName = signUpRequest.getFirstName();
        String lastName = signUpRequest.getLastName();
        String email = signUpRequest.getEmail();
        String password = signUpRequest.getPassword();
        String username = signUpRequest.getUsername();

        User user = new User(firstName, lastName, username,email, password );

        List<Role> roles = new ArrayList<>();

        roles.add(roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> new AppException(USER_ROLE_NOT_SET)));

        if (userRepository.count() == 0) { // Nếu là user đầu tiên, thêm quyền ADMIN
            roles.add(roleRepository.findByName(RoleName.ROLE_ADMIN)
                    .orElseThrow(() -> new AppException(USER_ROLE_NOT_SET)));
        }

        user.setRoles(roles);
        user.setRoles(roles);
        User savedUser = userService.addUser(user);
        URI location = ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/users/{userId}")
                .buildAndExpand(savedUser.getId()).toUri();
        return ResponseEntity.created(location).body(new ApiResponse(Boolean.TRUE, "User registered successfully"));
    }
    @PostMapping("/signin")
    public ResponseEntity<JwtAuthenticationResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate( //authenticationManager là điểm bắt đầu rồi sau đó gọi provider
                new UsernamePasswordAuthenticationToken(loginRequest.getUsernameOrEmail(),loginRequest.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication); // lưu thông tin xác thực vào securityContext
        String jwt = jwtTokenProvider.generateToken(authentication); // tạo jwt từ thng tin xác thực
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
    }
}
