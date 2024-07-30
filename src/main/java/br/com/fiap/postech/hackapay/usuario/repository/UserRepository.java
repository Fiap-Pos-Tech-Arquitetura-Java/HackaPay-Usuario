package br.com.fiap.postech.hackapay.usuario.repository;

import br.com.fiap.postech.hackapay.usuario.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByLogin(String login);
}
