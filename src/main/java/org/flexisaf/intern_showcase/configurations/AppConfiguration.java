package org.flexisaf.intern_showcase.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.flexisaf.intern_showcase.dto.UserDTO;
import org.flexisaf.intern_showcase.filters.AuthFilter;
import org.flexisaf.intern_showcase.filters.JwtFilter;
import org.flexisaf.intern_showcase.repository.UserRepository;
import org.flexisaf.intern_showcase.response.AuthResponse;
import org.flexisaf.intern_showcase.response.ErrorResponse;
import org.flexisaf.intern_showcase.service.JwtService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CorsConfigurer;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Configuration
@EnableWebSecurity
@Slf4j
public class AppConfiguration {


    static ObjectMapper objectMapper = new ObjectMapper();


    static String[] publicUrls = {
            "/api/user/create",
            "/api/user/login",
            "/admin/api/create",
            "/v3/api-docs/**",    // OpenAPI JSON
            "/swagger-ui.html",   // Swagger UI HTML entrypoint
            "/swagger-ui/**",     // Swagger UI resources (JS, CSS)
            "/webjars/**",        // (optional, legacy)
            "/actuator/**",
            "/admin/api/test",
            "/api/user/signup",
            "/api-service/login",
            "/api/user/signin",
            "/api/user/reset-ui",
            "/api/user/reset-password",
            "/api-service/account/create",
            "*.html","*.css","/css/**"
    };

    static String[] adminUrls = {
            "/admin/api/ambulance/**",
            "/admin/api/dashboard"
    };

    @Autowired
    private JwtService jwtService;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private JwtFilter jwtFilter;

    @Autowired
    private UserRepository userRepository;

    @Bean
    public ModelMapper getModelMapper() {
        return new ModelMapper();
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return myUserDetailsService;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider =
                new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(getPasswordEncoder());
        authenticationProvider.setUserDetailsService(userDetailsService());
        return authenticationProvider;
    }

    public AuthFilter authFilter(AuthenticationManager authenticationManager) {
        AuthFilter authFilter = new AuthFilter();
        authFilter.setFilterProcessesUrl("/api-service/login");
        authFilter.setAuthenticationManager(authenticationManager);
        authFilter.setAuthenticationSuccessHandler(((request, response, authentication) -> {
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            response.setStatus(HttpServletResponse.SC_OK);
            log.info("authenticated.....");

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

                String jwtToken = jwtService.generateJwtToken(
                        userRepository.findByEmail(userPrincipal.getUsername())
                                .map(data -> new UserDTO(data.getId(),data.getFullname(),
                                        data.getContactNum(),data.getMedicalDescription(),
                                        data.getEmail(),data.getPassword(),data.getRole())).orElse(new UserDTO()));

                AuthResponse authResponse = AuthResponse.builder()
                        .email(userPrincipal.getUsername())
                        .jwtToken(jwtToken)
                        .authenticated(true)
                        .build();

                response.getWriter().write(objectMapper.writeValueAsString(authResponse));

        }));

        authFilter.setAuthenticationFailureHandler(((request, response, exception) -> {
            log.info("an error occurred: {}",exception.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

                ErrorResponse errorResponse =
                        ErrorResponse.builder()
                                .message(exception.getMessage())
                                .throwable(exception.getCause())
                                .timeStamp(LocalDateTime.now().toString())
                                .build();
                response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
        }));

        return authFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity, AuthenticationManager authenticationManager   ) throws Exception {
        httpSecurity.csrf(CsrfConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(requests -> requests.
                        requestMatchers(adminUrls).hasRole("Admin")
                        .requestMatchers(publicUrls)
                        .permitAll().anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .maximumSessions(1))
                .addFilterAt(authFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        httpSecurity.formLogin(login -> login.loginPage("/api/user")
                .loginProcessingUrl("/api/user/login")
                .successHandler(new AuthSuccessHandler())
                .usernameParameter("email")
                .passwordParameter("password")
                .failureUrl("/api/user/error")
                .permitAll())
                .rememberMe(rememberMe -> rememberMe.
                        key(UUID.randomUUID().toString()).
                        rememberMeParameter("remember-me")
                        .tokenValiditySeconds(7 * 24 * 60 * 60)
                        .userDetailsService(userDetailsService())); // 7 days

        httpSecurity.logout(logout -> logout.logoutRequestMatcher(new AntPathRequestMatcher("/api/user/logout"))
                .logoutSuccessUrl("/api/user/signin")
                .logoutUrl("/api/user/logout")
                .clearAuthentication(true)
                .invalidateHttpSession(true)
                .permitAll());



        return  httpSecurity.build();

    }
}
