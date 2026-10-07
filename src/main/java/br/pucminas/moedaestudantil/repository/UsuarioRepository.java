package br.pucminas.moedaestudantil.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.pucminas.moedaestudantil.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
