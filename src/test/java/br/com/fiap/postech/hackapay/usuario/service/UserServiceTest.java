package br.com.fiap.postech.hackapay.usuario.service;

import br.com.fiap.postech.hackapay.security.JwtService;
import br.com.fiap.postech.hackapay.usuario.entity.User;
import br.com.fiap.postech.hackapay.usuario.helper.UserHelper;
import br.com.fiap.postech.hackapay.usuario.repository.UserRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtService jwtService;

    private AutoCloseable mock;

    @BeforeEach
    void setUp() {
        mock = MockitoAnnotations.openMocks(this);
        userService = new UserServiceImpl(userRepository, jwtService);
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    @Nested
    class CadastrarUser {
        @Test
        void devePermitirCadastrarUser() {
            // Arrange
            var user = UserHelper.getUser(false);
            when(userRepository.save(any(User.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            var userSalvo = userService.save(user);
            // Assert
            assertThat(userSalvo)
                    .isInstanceOf(User.class)
                    .isNotNull();
            assertThat(userSalvo.getLogin()).isEqualTo(user.getLogin());
            assertThat(userSalvo.getId()).isNotNull();
            verify(userRepository, times(1)).save(any(User.class));
        }


        @Test
        void deveGerarExcecao_QuandoCadastrarUser_loginExistente() {
            // Arrange
            var user = UserHelper.getUser(true);
            when(userRepository.findByLogin(user.getLogin())).thenReturn(Optional.of(user));
            // Act
            assertThatThrownBy(() -> userService.save(user))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Já existe um user cadastrado com esse login.");
            // Assert
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    class BuscarUser {
        @Test
        void devePermitirBuscarUserPorId() {
            // Arrange
            var user = UserHelper.getUser(true);
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            // Act
            var userObtido = userService.findById(user.getId());
            // Assert
            assertThat(userObtido).isEqualTo(user);
            verify(userRepository, times(1)).findById(any(UUID.class));
        }
        @Test
        void devePermitirBuscarUserPorLogin() {
            // Arrange
            var user = UserHelper.getUser(true);
            when(userRepository.findByLogin(user.getLogin())).thenReturn(Optional.of(user));
            // Act
            var userObtido = userService.findByLogin(user.getLogin());
            // Assert
            assertThat(userObtido).isEqualTo(user);
            verify(userRepository, times(1)).findByLogin(anyString());
        }

        @Test
        void deveGerarExcecao_QuandoBuscarUserPorId_idNaoExiste() {
            // Arrange
            var user = UserHelper.getUser(true);
            when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
            UUID uuid = user.getId();
            // Act
            assertThatThrownBy(() -> userService.findById(uuid))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("User não encontrado com o ID: " + user.getId());
            // Assert
            verify(userRepository, times(1)).findById(any(UUID.class));
        }

        @Test
        void devePermitirBuscarTodosUser() {
            // Arrange
            User criteriosDeBusca = UserHelper.getUser(false);
            Page<User> users = new PageImpl<>(Arrays.asList(
                    UserHelper.getUser(true),
                    UserHelper.getUser(true),
                    UserHelper.getUser(true)
            ));
            when(userRepository.findAll(any(Example.class), any(Pageable.class))).thenReturn(users);
            // Act
            var usersObtidos = userService.findAll(Pageable.unpaged(), criteriosDeBusca);
            // Assert
            assertThat(usersObtidos).hasSize(3);
            assertThat(usersObtidos.getContent()).asList().allSatisfy(
                    user -> {
                        assertThat(user)
                                .isNotNull()
                                .isInstanceOf(User.class);
                    }
            );
            verify(userRepository, times(1)).findAll(any(Example.class), any(Pageable.class));
        }
    }

    @Nested
    class AlterarUser {
        @Test
        void devePermitirAlterarUser() {
            // Arrange
            var user = UserHelper.getUser(true);
            var userReferencia = new User(user.getLogin(), user.getPassword());
            var novoUser = new User(
                    user.getLogin(),
                    user.getPassword()
            );
            novoUser.setId(user.getId());
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            var userSalvo = userService.update(user.getId(), novoUser);
            // Assert
            assertThat(userSalvo)
                    .isInstanceOf(User.class)
                    .isNotNull();
            assertThat(userSalvo.getPassword()).isEqualTo(novoUser.getPassword());
            assertThat(userSalvo.getPassword()).isNotEqualTo(userReferencia.getPassword());

            verify(userRepository, times(1)).findById(any(UUID.class));
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        void devePermitirAlterarUser_semBody() {
            // Arrange
            var user = UserHelper.getUser(true);
            var userReferencia = new User(user.getLogin(), user.getPassword());
            var novoUser = new User(null, null);

            novoUser.setId(user.getId());
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            var userSalvo = userService.update(user.getId(), novoUser);
            // Assert
            assertThat(userSalvo)
                    .isInstanceOf(User.class)
                    .isNotNull();
            assertThat(userSalvo.getLogin()).isEqualTo(userReferencia.getLogin());

            verify(userRepository, times(1)).findById(any(UUID.class));
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarUserPorId_idNaoExiste() {
            // Arrange
            var user = UserHelper.getUser(true);
            when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
            UUID uuid = user.getId();
            // Act && Assert
            assertThatThrownBy(() -> userService.update(uuid, user))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("User não encontrado com o ID: " + user.getId());
            verify(userRepository, times(1)).findById(any(UUID.class));
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarUserPorId_alterandoId() {
            // Arrange
            var user = UserHelper.getUser(true);
            var userParam = UserHelper.getUser(true);
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            UUID uuid = user.getId();
            // Act && Assert
            assertThatThrownBy(() -> userService.update(uuid, userParam))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Não é possível alterar o id de um user.");
            verify(userRepository, times(1)).findById(any(UUID.class));
            verify(userRepository, never()).save(any(User.class));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarUserPorId_alterandoLogin() {
            // Arrange
            var user = UserHelper.getUser(true);
            var userParam = UserHelper.getUser(true);
            userParam.setId(user.getId());
            userParam.setLogin(user.getLogin() + "x");
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            UUID uuid = user.getId();
            // Act && Assert
            assertThatThrownBy(() -> userService.update(uuid, userParam))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Não é possível alterar o login de um user.");
            verify(userRepository, times(1)).findById(any(UUID.class));
            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    class RemoverUser {
        @Test
        void devePermitirRemoverUser() {
            // Arrange
            var user = UserHelper.getUser(true);
            when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
            doNothing().when(userRepository).deleteById(user.getId());
            // Act
            userService.delete(user.getId());
            // Assert
            verify(userRepository, times(1)).findById(any(UUID.class));
            verify(userRepository, times(1)).deleteById(any(UUID.class));
        }

        @Test
        void deveGerarExcecao_QuandRemoverUserPorId_idNaoExiste() {
            // Arrange
            var user = UserHelper.getUser(true);
            doNothing().when(userRepository).deleteById(user.getId());
            UUID uuid = user.getId();
            // Act && Assert
            assertThatThrownBy(() -> userService.delete(uuid))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("User não encontrado com o ID: " + user.getId());
            verify(userRepository, times(1)).findById(any(UUID.class));
            verify(userRepository, never()).deleteById(any(UUID.class));
        }
    }

    @Nested
    class LoginUser {
        @Test
        void devePermitirLoginUser() throws Exception {
            // Arrange
            var user = UserHelper.getUser(true);
            var token = UserHelper.getToken(user);
            when(userRepository.findByLogin(user.getLogin())).thenReturn(Optional.of(user));
            when(jwtService.generateToken(any(br.com.fiap.postech.hackapay.security.User.class))).thenReturn(token);
            var userLogin = new User(
                    user.getLogin(),
                    "123456"
            );
            // Act
            var tokenObtido = userService.login(userLogin);
            // Assert
            assertThat(tokenObtido).isNotNull();
            assertThat(tokenObtido.accessToken()).isNotNull();
            assertThat(tokenObtido.erro()).isNull();
            verify(userRepository, times(1)).findByLogin(anyString());
            verify(jwtService, times(1)).generateToken(any(br.com.fiap.postech.hackapay.security.User.class));
        }

        @Test
        void deveGerarExcecao_QuandoLoginUserInexistente() throws Exception {
            // Arrange
            var user = UserHelper.getUser(true);
            when(userRepository.findByLogin(user.getLogin())).thenReturn(Optional.empty());
            // Act
            assertThatThrownBy(() -> userService.login(user))
                    .isInstanceOf(Exception.class)
                    .hasMessage("Usuario informado não encontrado.");
            // Assert
            verify(userRepository, times(1)).findByLogin(anyString());
        }

        @Test
        void deveGerarExcecao_QuandoLoginUserSenhaErrada() throws Exception {
            // Arrange
            var user = UserHelper.getUser(true);
            user.setPassword("senha_errada");
            when(userRepository.findByLogin(user.getLogin())).thenReturn(Optional.of(user));
            // Act
            assertThatThrownBy(() -> userService.login(user))
                    .isInstanceOf(Exception.class)
                    .hasMessage("Senha do User informado não confere.");
            // Assert
            verify(userRepository, times(1)).findByLogin(anyString());
        }
    }
}
