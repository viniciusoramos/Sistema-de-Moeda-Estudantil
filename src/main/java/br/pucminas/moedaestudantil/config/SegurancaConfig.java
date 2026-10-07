package br.pucminas.moedaestudantil.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Senhas são gravadas apenas como hash BCrypt (RNF04).
 * O fluxo de login/sessão será ligado na Sprint 3 (Lab03S03).
 */
@Configuration
public class SegurancaConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
