package sesac.bookmanager.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import sesac.bookmanager.security.AuthAdminDetailService;
import sesac.bookmanager.security.AuthUserDetailService;
import sesac.bookmanager.security.JwtAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthUserDetailService authUserDetailService;
    private final AuthAdminDetailService authAdminDetailService;
    /**
     * ✅ 1) 관리자용 (세션 기반)
     * 경로: /admin/**
     */
    @Bean
    @Order(1)
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/admin/**")
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // 세션 기반은 CSRF 활성화
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/admin/auth/login").permitAll()
                        .anyRequest().hasRole("ADMIN") // anyRequest는 반드시 마지막에
                )
                // 필터 내용 포함
                .formLogin(form -> form
                        .loginPage("/admin/auth/login")
                        .usernameParameter("accountId")   // 🔑 여기! 기본 "username" → "email"
                        .passwordParameter("password") // 기본은 "password", 그대로 쓰면 됨
                        .defaultSuccessUrl("/admin/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/admin/auth/logout")
                        .logoutSuccessUrl("/admin/auth/login")
                )
                .authenticationManager(adminAuthenticationManager());

        return http.build();
    }
    @Bean
    public AuthenticationManager adminAuthenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(authAdminDetailService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }

    /**
     * ✅ 2) 유저용 (JWT)
     * 경로: /api/**
     */
    @Bean
    @Order(2) // 포함 관계 때문에 넓은 경로는 나중에 매칭
    public SecurityFilterChain userSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .securityMatcher("/api/**")
                .csrf(AbstractHttpConfigurer::disable) // JWT는 CSRF 필요 없음
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 상태 유지 안 함
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/","/home","/api/auth/**","/api/notice/**",
                                "/api/question/**","/api/reply/**","/api/v1/books/**").permitAll()
                        .anyRequest().authenticated() // 그 외는 인증 필요
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // JWT 인증 필터 추가
                .authenticationManager(userAuthenticationManager());
        return http.build();
    }
    @Bean
    @Primary
    public AuthenticationManager userAuthenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(authUserDetailService);
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }


    /**
     * 공통 비밀번호 인코더
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



}
