package com.tassisfernando.chamegoapi.infrastructure.config;

import com.tassisfernando.chamegoapi.domain.usuario.Usuario;
import com.tassisfernando.chamegoapi.domain.usuario.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Garante o usuário admin no perfil local (H2 sem Flyway) e em ambientes
 * onde a seed da migration ainda não rodou.
 */
@Component
public class UsuarioAdminInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(UsuarioAdminInitializer.class);

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioAdminInitializer(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (usuarioRepository.contar() > 0) {
            return;
        }
        usuarioRepository.salvar(Usuario.novo("admin", passwordEncoder.encode("chamego123")));
        log.info("Usuário admin inicial criado (login=admin).");
    }
}
