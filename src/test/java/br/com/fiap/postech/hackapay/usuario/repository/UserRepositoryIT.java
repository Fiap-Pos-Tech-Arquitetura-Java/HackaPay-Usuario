package br.com.fiap.postech.hackapay.usuario.repository;

import br.com.fiap.postech.hackapay.usuario.entity.User;
import br.com.fiap.postech.hackapay.usuario.helper.UserHelper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
public class UserRepositoryIT {
    private final UserRepository userRepository;

    @Autowired
    public UserRepositoryIT(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Test
    void devePermitirCriarEstrutura() {
        var totalRegistros = userRepository.count();
        assertThat(totalRegistros).isEqualTo(4);
    }

    @Test
    void devePermitirCadastrarUser() {
        // Arrange
        var user = UserHelper.getUser(true);
        // Act
        var userCadastrado = userRepository.save(user);
        // Assert
        assertThat(userCadastrado).isInstanceOf(User.class).isNotNull();
        assertThat(userCadastrado.getId()).isEqualTo(user.getId());
        assertThat(userCadastrado.getLogin()).isEqualTo(user.getLogin());
    }
    @Test
    void devePermitirBuscarUser() {
        // Arrange
        var id = UUID.fromString("7a04f6fb-c79b-4b47-af54-9bef34cbab35");
        var login = "anderson.wagner@gmail.com";
        // Act
        var userOpcional = userRepository.findById(id);
        // Assert
        assertThat(userOpcional).isPresent();
        userOpcional.ifPresent(
                userRecebido -> {
                    assertThat(userRecebido).isInstanceOf(User.class).isNotNull();
                    assertThat(userRecebido.getId()).isEqualTo(id);
                    assertThat(userRecebido.getLogin()).isEqualTo(login);
                }
        );
    }
    @Test
    void devePermitirRemoverUser() {
        // Arrange
        var id = UUID.fromString("f6497965-3cf0-4601-a631-01878ef70f40");
        // Act
        userRepository.findById(id).orElseThrow();
        userRepository.deleteById(id);
        // Assert
        var userOpcional = userRepository.findById(id);
        assertThat(userOpcional).isEmpty();
    }
    @Test
    void devePermitirListarUsers() {
        // Arrange
        // Act
        var usersListados = userRepository.findAll();
        // Assert
        assertThat(usersListados).hasSize(4);
    }
}
