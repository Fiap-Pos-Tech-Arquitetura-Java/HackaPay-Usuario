package br.com.fiap.postech.hackapay.usuario.repository;

import br.com.fiap.postech.hackapay.usuario.entity.User;
import br.com.fiap.postech.hackapay.usuario.helper.UserHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserRepositoryTest {
    @Mock
    private UserRepository userRepository;

    AutoCloseable openMocks;
    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        openMocks.close();
    }

    @Test
    void devePermitirCadastrarUser() {
        // Arrange
        var user = UserHelper.getUser(false);
        when(userRepository.save(any(User.class))).thenReturn(user);
        // Act
        var savedUser = userRepository.save(user);
        // Assert
        assertThat(savedUser).isNotNull().isEqualTo(user);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void devePermitirBuscarUser() {
        // Arrange
        var user = UserHelper.getUser(true);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        // Act
        var userOpcional = userRepository.findById(user.getId());
        // Assert
        assertThat(userOpcional).isNotNull().containsSame(user);
        userOpcional.ifPresent(
                userRecebido -> {
                    assertThat(userRecebido).isInstanceOf(User.class).isNotNull();
                    assertThat(userRecebido.getId()).isEqualTo(user.getId());
                    assertThat(userRecebido.getLogin()).isEqualTo(user.getLogin());
                }
        );
        verify(userRepository, times(1)).findById(user.getId());
    }
    @Test
    void devePermitirRemoverUser() {
        //Arrange
        var id = UUID.randomUUID();
        doNothing().when(userRepository).deleteById(id);
        //Act
        userRepository.deleteById(id);
        //Assert
        verify(userRepository, times(1)).deleteById(id);
    }
    @Test
    void devePermitirListarUsers() {
        // Arrange
        var user1 = UserHelper.getUser(true);
        var user2 = UserHelper.getUser(true);
        var listaUsers = Arrays.asList(
                user1,
                user2
        );
        when(userRepository.findAll()).thenReturn(listaUsers);
        // Act
        var usersListados = userRepository.findAll();
        assertThat(usersListados)
                .hasSize(2)
                .containsExactlyInAnyOrder(user1, user2);
        verify(userRepository, times(1)).findAll();
    }
}