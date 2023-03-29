package ru.artemchegushka.restjwtauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.artemchegushka.restjwtauth.exception.MyAuthEntryPoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JWTAuthenticationFilter jwtFilter;
    private final AuthenticationProvider authProvider;
    private final MyAuthEntryPoint myAuthEntryPoint;
    private final Logout logout;


    @Autowired
    public SecurityConfig(JWTAuthenticationFilter jwtFilter, AuthenticationProvider authProvider, MyAuthEntryPoint myAuthEntryPoint, Logout logout) {
        this.jwtFilter = jwtFilter;
        this.authProvider = authProvider;
        this.myAuthEntryPoint = myAuthEntryPoint;

        this.logout = logout;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf()
                .disable()
                .authorizeHttpRequests()
                .requestMatchers("/auth/**")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
//                Две эти строки нужны для того что бы перехватывать событие, когда не авторизованный или
//                не зарегистрированный пользователь пытается получить доступ, мы тогда выдаем сообщение
                .exceptionHandling()
                .authenticationEntryPoint(myAuthEntryPoint)
                .and()
//                Отключаем сессию
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authenticationProvider(authProvider)
//             Добавили наш фильтр jwt в начало цепочки фильтров
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .logout()
                .logoutUrl("/auth/logout")
                .addLogoutHandler(logout)
                .logoutSuccessHandler(((request, response, authentication) -> SecurityContextHolder.clearContext()));
        return httpSecurity.build();

    }


}
