package org.example.libraryms.Security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/v1/auth/**").permitAll()
                        .requestMatchers("/events").permitAll()

                        //GET requires authen with any ROLE
                        .requestMatchers(HttpMethod.GET,"/v1/books").authenticated()
                        .requestMatchers("/v1/books/**").hasAnyRole("ADMIN", "LIBRARIAN")

                        //GET requires authen with any ROLE
                        .requestMatchers(HttpMethod.GET,"/v1/category").authenticated()
                        .requestMatchers("/v1/category/**").hasAnyRole("ADMIN", "LIBRARIAN")

                        /*
                         * GET profile + ChangePassword + UPDATE requires authen with any ROLE
                         * GET users + CREATE + DELETE hasAnyRole(ADMIN, LIBRARIAN)
                         */
                        .requestMatchers("/v1/users/profile/**").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/v1/users/**").authenticated()
                        .requestMatchers("/v1/users/**").hasAnyRole("ADMIN", "LIBRARIAN")

                        /* GET(transaction + items) requires authen with any ROLE
                         * CREATE + PUT(returnBook + update) + DELETE hasAnyRole(ADMIN, LIBRARIAN)
                         */
                        .requestMatchers(HttpMethod.GET,"/v1/transactions/**").authenticated()
                        .requestMatchers("/v1/transactions/**").hasAnyRole("ADMIN", "LIBRARIAN")

                        /* GET(search + items) requires authen with any ROLE
                         * POST(create) BORROWER only
                         * PUT(approve/taken/reject) ADMIN, LIBRARIAN only
                         * DELETE(cancel) BORROWER only
                         */
                        .requestMatchers(HttpMethod.GET, "/v1/borrow-requests/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/v1/borrow-requests").hasRole("BORROWER")
                        .requestMatchers(HttpMethod.DELETE, "/v1/borrow-requests/**").hasRole("BORROWER")
                        .requestMatchers(HttpMethod.PUT, "/v1/borrow-requests/**").hasAnyRole("ADMIN", "LIBRARIAN")

                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(false);
        return new UrlBasedCorsConfigurationSource() {{
            registerCorsConfiguration("/**", configuration);
        }};
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
