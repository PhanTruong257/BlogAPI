package study.blogapi.config;

import org.springframework.security.config.Customizer;
import study.blogapi.security.JwtAuthenticationEntryPoint;
import study.blogapi.security.JwtAuthenticationFilter;
import study.blogapi.security.Oauth2.OAuth2Success;
import study.blogapi.service.impl.CustomUserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableGlobalMethodSecurity(
		securedEnabled = true,
		jsr250Enabled = true,
		prePostEnabled = true)
public class SecurityConfig {
	private final PasswordEncoder passwordEncoder;

	private final CustomUserDetailsServiceImpl customUserDetailsService;
	private final JwtAuthenticationEntryPoint unauthorizedHandler;
	private final JwtAuthenticationFilter jwtAuthenticationFilter;
	private final OAuth2Success oAuth2Success;

	public SecurityConfig(PasswordEncoder passwordEncoder, CustomUserDetailsServiceImpl customUserDetailsService,
                          JwtAuthenticationEntryPoint unauthorizedHandler,
                          JwtAuthenticationFilter jwtAuthenticationFilter, OAuth2Success oAuth2Success) {
        this.passwordEncoder = passwordEncoder;
        this.customUserDetailsService = customUserDetailsService;
		this.unauthorizedHandler = unauthorizedHandler;
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.oAuth2Success = oAuth2Success;
    }
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.cors(cors -> cors.configure(http)) // Cấu hình CORS mới
				.csrf(csrf -> csrf.disable()) // Disable CSRF nếu cần
				.exceptionHandling(eh -> eh.authenticationEntryPoint(unauthorizedHandler))
				.sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.GET, "/api/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
						.requestMatchers("/oauth2/authorization/**").permitAll()
						.requestMatchers(HttpMethod.GET, "/api/users/checkUsernameAvailability", "/api/users/checkEmailAvailability").permitAll()
						.anyRequest().authenticated()

				)
				.oauth2Login(oauth2 -> oauth2
						.successHandler(oAuth2Success) // Custom xử lý khi login OAuth2 thành công

				);
		http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager() { // provider gọi DaoAuthenticationProvider thực hiện xác thực
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(customUserDetailsService); // chỉ định custoimUserDetail làm database
		authProvider.setPasswordEncoder(passwordEncoder);// nếu user tồn tại thì kiểm tra password
		return new ProviderManager(List.of(authProvider));
	}

}
