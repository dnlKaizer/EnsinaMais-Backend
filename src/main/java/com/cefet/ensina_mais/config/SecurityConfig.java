package com.cefet.ensina_mais.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import com.cefet.ensina_mais.security.JwtAuthenticationFilter;
import com.cefet.ensina_mais.services.UsuarioDetailsService;

@Configuration
public class SecurityConfig {
    @Autowired
    private UsuarioDetailsService usuarioDetailsService;
    
    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                .requestMatchers(HttpMethod.POST, "/usuarios").permitAll()
                .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()

                // Alunos - Regras específicas primeiro
                .requestMatchers(HttpMethod.GET, "/alunos/{alunoId}/matriculas/{matriculaId}/matriculas-turmas").hasAnyRole("ADMIN", "PROFESSOR", "ALUNO")
                .requestMatchers(HttpMethod.GET, "/alunos/{alunoId}/matriculas-turmas/{matriculaTurmaId}/notas").hasAnyRole("ADMIN", "PROFESSOR", "ALUNO")
                .requestMatchers(HttpMethod.GET, "/alunos/{alunoId}/matriculas-turmas/{matriculaTurmaId}/disciplina").hasAnyRole("ADMIN", "PROFESSOR", "ALUNO")
                .requestMatchers(HttpMethod.GET, "/alunos/{alunoId}/matriculas-turmas/{matriculaTurmaId}/professor").hasAnyRole("ADMIN", "PROFESSOR", "ALUNO")
                .requestMatchers(HttpMethod.GET, "/alunos/{alunoId}/matriculas-turmas/{matriculaTurmaId}/avaliacoes").hasAnyRole("ADMIN", "PROFESSOR", "ALUNO")
                .requestMatchers(HttpMethod.GET, "/alunos/{alunoId}/matriculas-turmas/{matriculaTurmaId}/turma").hasAnyRole("ADMIN", "PROFESSOR", "ALUNO")
                .requestMatchers(HttpMethod.GET, "/alunos/{id}/matriculas").hasAnyRole("ADMIN", "PROFESSOR", "ALUNO")
                .requestMatchers(HttpMethod.GET, "/alunos/{id}/matriculas-turmas").hasAnyRole("ADMIN", "PROFESSOR", "ALUNO")
                .requestMatchers(HttpMethod.GET, "/alunos/{id}/notas").hasAnyRole("ADMIN", "PROFESSOR", "ALUNO")
                .requestMatchers(HttpMethod.GET, "/alunos/usuario/{usuarioId}").hasAnyRole("ADMIN", "ALUNO")
                .requestMatchers(HttpMethod.GET, "/alunos/usuario/login/{usuarioLogin}").hasAnyRole("ADMIN", "ALUNO")
                .requestMatchers(HttpMethod.GET, "/alunos/{id}").hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers(HttpMethod.POST, "/alunos").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/alunos/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/alunos/**").hasRole("ADMIN")

                // Professores
                .requestMatchers(HttpMethod.GET, "/professores/{id}").hasAnyRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/professores/{usuarioId}").hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers(HttpMethod.GET, "/professores/{id}/turmas").hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers(HttpMethod.GET, "/professores/{id}/alunos").hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers(HttpMethod.GET, "/professores/{id}/notas").hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers(HttpMethod.GET, "/professores/{id}/avaliacoes").hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers(HttpMethod.PUT, "/professores/{professorId}/notas/{notaId}").hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers(HttpMethod.POST, "/professores/{professorId}/avaliacoes").hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers(HttpMethod.POST, "/professores").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/professores/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/professores/**").hasRole("ADMIN")

                // Disciplinas
                .requestMatchers(HttpMethod.GET, "/disciplinas/**").hasAnyRole("ADMIN", "PROFESSOR", "ALUNO")
                .requestMatchers(HttpMethod.POST, "/disciplinas").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/disciplinas/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/disciplinas/**").hasRole("ADMIN")

                // Matrículas
                .requestMatchers(HttpMethod.GET, "/matriculas/**").hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers(HttpMethod.POST, "/matriculas").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/matriculas/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/matriculas/**").hasRole("ADMIN")

                // MatrículaTurma
                .requestMatchers(HttpMethod.GET, "/matriculas-turmas/**").hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers(HttpMethod.POST, "/matriculas-turmas").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/matriculas-turmas/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/matriculas-turmas/**").hasRole("ADMIN")

                // Turmas
                .requestMatchers(HttpMethod.GET, "/turmas/**").hasAnyRole("ADMIN", "PROFESSOR", "ALUNO")
                .requestMatchers(HttpMethod.POST, "/turmas").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/turmas/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/turmas/**").hasRole("ADMIN")

                // Avaliações
                .requestMatchers(HttpMethod.GET, "/avaliacoes/**").hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers(HttpMethod.POST, "/avaliacoes").hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers(HttpMethod.PUT, "/avaliacoes/**").hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers(HttpMethod.DELETE, "/avaliacoes/**").hasAnyRole("ADMIN", "PROFESSOR")

                // Notas
                .requestMatchers(HttpMethod.GET, "/notas").hasAnyRole("ADMIN", "PROFESSOR")
                .requestMatchers(HttpMethod.GET, "/notas/{id}").hasAnyRole("ADMIN", "PROFESSOR", "ALUNO")
                .requestMatchers(HttpMethod.POST, "/notas").hasRole("ADMIN")
                .requestMatchers(HttpMethod.PUT, "/notas/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/notas/**").hasRole("ADMIN")

                .anyRequest().authenticated() // Todas as outras requisições precisam de autenticação
            )
            .headers(headers -> headers.frameOptions().disable())
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(usuarioDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}