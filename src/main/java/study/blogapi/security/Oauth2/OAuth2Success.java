package study.blogapi.security.Oauth2;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import study.blogapi.model.role.Role;

import study.blogapi.exception.AppException;
import study.blogapi.model.role.Role;
import study.blogapi.model.role.RoleName;
import study.blogapi.model.user.User;
import study.blogapi.repository.RoleRepository;
import study.blogapi.repository.UserRepository;
import study.blogapi.security.JwtTokenProvider;
import study.blogapi.security.UserPrincipal;
import study.blogapi.service.UserService;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class OAuth2Success extends SimpleUrlAuthenticationSuccessHandler {
    private static final String USER_ROLE_NOT_SET = "User role not set";

    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public OAuth2Success(JwtTokenProvider tokenProvider, UserService userService, RoleRepository roleRepository, UserRepository userRepository) {
        this.tokenProvider = tokenProvider;
        this.userService = userService;

        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // Lấy Principal từ Authentication
        Object principal = authentication.getPrincipal();


        // Kiểm tra xem principal có phải là OAuth2User không (nếu cần thiết)
        if (principal instanceof OAuth2User oAuth2User) {
            // Truy cập các thuộc tính của OAuth2User
            String email = (String) oAuth2User.getAttributes().get("email");
            String name = (String) oAuth2User.getAttributes().get("name");

            // In thông tin email và name ra
            System.out.println("User email: " + email);
            System.out.println("User name: " + name);

// Tách tên thành các phần (tên đệm, tên, họ) dựa trên khoảng trắng
            String[] nameParts = name.split(" ");

// Giả sử tên đệm là phần đầu tiên và họ là phần cuối cùng
            String firstName = nameParts[0];  // Tên đệm
            String lastName = nameParts[nameParts.length - 1];  // Họ
            // Kiểm tra user trong database
            boolean isUserAvailable = userService.checkEmailAvailability(email).getAvailable();
            System.out.println("User available: " + isUserAvailable);

            if (isUserAvailable) {
                String password = UUID.randomUUID().toString().substring(0, 10);
                User user = new User(firstName, lastName, email,email, password );
                List<Role> roles = new ArrayList<>();

                roles.add(roleRepository.findByName(RoleName.ROLE_USER)
                        .orElseThrow(() -> new AppException(USER_ROLE_NOT_SET)));

                if (userRepository.count() == 0) { // Nếu là user đầu tiên, thêm quyền ADMIN
                    roles.add(roleRepository.findByName(RoleName.ROLE_ADMIN)
                            .orElseThrow(() -> new AppException(USER_ROLE_NOT_SET)));
                }

                user.setRoles(roles);
                user.setRoles(roles);
                userService.addUser(user);
            }



            // Tạo JWT token từ email
            String token = tokenProvider.generateTokenFromEmail(email);

            // Trả về token dưới dạng JSON
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"token\": \"" + token + "\"}");
        } else {
            throw new IllegalStateException("Principal is not an OAuth2User");
        }
    }

}