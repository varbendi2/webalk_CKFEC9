package edu.sabanciuniv.hotelbookingapp.config;

import edu.sabanciuniv.hotelbookingapp.security.CustomAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    // Jelszó titkosítása BCrypt kódolással
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Biztonsági szabályok konfigurálása
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        // CSRF védelem kikapcsolása teszteléshez
        http.csrf(csrf -> csrf.disable());

        http.authorizeHttpRequests((authorize) -> 
                        authorize.requestMatchers("/css/**", "/js/**", "/webjars/**", "/img/**").permitAll()  // Statikus fájlok elérése mindenki számára
                                .requestMatchers("/", "/index", "/register/**").permitAll()  // Regisztrációs és kezdőlap szabad hozzáférés
                                .requestMatchers("/admin/**").hasRole("ADMIN")  // Adminisztrátorok számára
                                .requestMatchers("/customer/**").hasRole("CUSTOMER")  // Vásárlók számára
                                .requestMatchers("/manager/**").hasRole("HOTEL_MANAGER")  // Szállodai menedzserek számára
                                .anyRequest().authenticated())  // Minden egyéb kérést autentikálni kell
                .formLogin(
                        form -> form
                                .loginPage("/login")  // Bejelentkezési oldal
                                .loginProcessingUrl("/login")  // Bejelentkezési URL
                                .successHandler(customAuthenticationSuccessHandler)  // Sikeres bejelentkezés kezelése
                                .permitAll())  // Mindenki számára elérhető
                .logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))  // Kijelentkezési URL
                                .permitAll()  // Mindenki számára elérhető
                );
        return http.build();
    }

    // Globális autentikációs konfiguráció
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)  // Felhasználói adatok szolgáltatása
                .passwordEncoder(passwordEncoder());  // Jelszó titkosítása
    }

}
