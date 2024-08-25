package com.oasis.binary_honam.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oasis.binary_honam.config.auth.AuthFailureHandler;
import com.oasis.binary_honam.config.auth.PrincipalDetailsService;
import com.oasis.binary_honam.config.security.CustomSessionExpiredStrategy;
import com.oasis.binary_honam.entity.enums.Role;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthFailureHandler authFailureHandler;
    private final PrincipalDetailsService principalDetailsService;
    private final CustomSessionExpiredStrategy customSessionExpiredStrategy;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.cors(corsCustomizer -> corsCustomizer.configurationSource(corsConfigurationSource()))
                .csrf((csrfConfig) -> csrfConfig.disable())
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**").permitAll()
                        .requestMatchers( "/join", "/login", "/logout", "/api/user/find-email", "/api/user/temp-password").permitAll()
                        .requestMatchers("/api/**").hasAnyRole(Role.USER.name(), Role.ADMIN.name())
                        .requestMatchers("/admin/**").hasRole(Role.ADMIN.name())
                        .anyRequest().authenticated()
                )
                .formLogin((formLogin) ->
                        formLogin
                                .loginProcessingUrl("/login")
                                .usernameParameter("email")
                                .passwordParameter("password")
                                .successHandler((request, response, authentication) -> {
                                    response.setStatus(HttpServletResponse.SC_OK);
                                    response.setContentType("application/json");
                                    response.setCharacterEncoding("UTF-8");

                                    Map<String, Object> responseMap = new HashMap<>();
                                    responseMap.put("message", "로그인 성공");
                                    responseMap.put("user", authentication.getName());

                                    PrintWriter writer = response.getWriter();
                                    writer.write(new ObjectMapper().writeValueAsString(responseMap));
                                    writer.flush();
                                })
                                .failureHandler(authFailureHandler)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(HttpServletResponse.SC_OK);
                            response.setContentType("application/json");
                            response.setCharacterEncoding("UTF-8");

                            Map<String, Object> responseMap = new HashMap<>();
                            responseMap.put("message", "로그아웃 성공");

                            PrintWriter writer = response.getWriter();
                            writer.write(new ObjectMapper().writeValueAsString(responseMap));
                            writer.flush();
                        })
                )
                // 세션 관리 설정 추가
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                        .sessionFixation().migrateSession()
                        .invalidSessionUrl("/login")
                        .maximumSessions(1)
//                        .expiredUrl("/login")
                        .expiredSessionStrategy(customSessionExpiredStrategy)
                )
                .userDetailsService(principalDetailsService);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("HEAD","POST","GET","DELETE","PUT"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}