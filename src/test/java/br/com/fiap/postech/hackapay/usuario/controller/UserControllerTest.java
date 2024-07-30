package br.com.fiap.postech.hackapay.usuario.controller;

import br.com.fiap.postech.hackapay.security.SecurityHelper;
import br.com.fiap.postech.hackapay.usuario.entity.User;
import br.com.fiap.postech.hackapay.usuario.helper.UserHelper;
import br.com.fiap.postech.hackapay.usuario.security.Token;
import br.com.fiap.postech.hackapay.usuario.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {
    public static final String USER = "/user";
    private MockMvc mockMvc;
    @Mock
    private UserService userService;
    @Mock
    private SecurityHelper securityHelper;

    private AutoCloseable mock;

    @BeforeEach
    void setUp() {

        mock = MockitoAnnotations.openMocks(this);
        UserController userController = new UserController(userService, securityHelper);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    public static String asJsonString(final Object object) throws Exception {
        return new ObjectMapper().writeValueAsString(object);
    }

    @Nested
    class CadastrarUser {
        @Test
        void devePermitirCadastrarUser() throws Exception {
            // Arrange
            var user = UserHelper.getUser(false);
            when(userService.save(any(User.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            mockMvc.perform(
                            post(USER).contentType(MediaType.APPLICATION_JSON)
                                    .content(asJsonString(user)))
                    .andExpect(status().isCreated());
            // Assert
            verify(userService, times(1)).save(any(User.class));
        }

        @Test
        void deveGerarExcecao_QuandoRegistrarUser_RequisicaoXml() throws Exception {
            // Arrange
            var user = UserHelper.getUser(false);
            when(userService.save(any(User.class))).thenAnswer(r -> r.getArgument(0));
            // Act
            mockMvc.perform(
                            post("/user").contentType(MediaType.APPLICATION_XML)
                                    .content(asJsonString(user)))
                    .andExpect(status().isUnsupportedMediaType());
            // Assert
            verify(userService, never()).save(any(User.class));
        }
    }
    @Nested
    class BuscarUser {
        @Test
        void devePermitirBuscarUserPorId() throws Exception {
            // Arrange
            var user = UserHelper.getUser(true);
            when(userService.findById(any(UUID.class))).thenReturn(user);
            // Act
            mockMvc.perform(get("/user/{id}", user.getId().toString()))
                    .andExpect(status().isOk());
            // Assert
            verify(userService, times(1)).findById(any(UUID.class));
        }
        @Test
        void devePermitirBuscarUserPorLogin() throws Exception {
            // Arrange
            var user = UserHelper.getUser(true);
            when(userService.findByLogin(anyString())).thenReturn(user);
            // Act
            mockMvc.perform(get("/user/findByLogin/{login}", user.getLogin()))
                    .andExpect(status().isOk());
            // Assert
            verify(userService, times(1)).findByLogin(anyString());
        }
        @Test
        void deveGerarExcecao_QuandoBuscarUserPorId_idNaoExiste() throws Exception {
            // Arrange
            var user = UserHelper.getUser(true);
            when(userService.findById(user.getId())).thenThrow(IllegalArgumentException.class);
            // Act
            mockMvc.perform(get("/user/{id}", user.getId().toString()))
                    .andExpect(status().isBadRequest());
            // Assert
            verify(userService, times(1)).findById(user.getId());
        }

        @Test
        void devePermitirBuscarTodosUser() throws Exception {
            // Arrange
            int page = 0;
            int size = 10;
            var user = UserHelper.getUser(true);
            var criterioUser = new User(user.getLogin(), null);
            criterioUser.setId(null);
            List<User> listUser = new ArrayList<>();
            listUser.add(user);
            Page<User> users = new PageImpl<>(listUser);
            var pageable = PageRequest.of(page, size);
            when(userService.findAll(
                            pageable,
                            criterioUser
                    )
            ).thenReturn(users);
            // Act
            mockMvc.perform(
                            get("/user")
                                    .param("page", String.valueOf(page))
                                    .param("size", String.valueOf(size))
                                    .param("usuario", user.getLogin())
                    )
                    //.andDo(print())
                    .andExpect(status().is5xxServerError())
            //.andExpect(jsonPath("$.content", not(empty())))
            //.andExpect(jsonPath("$.totalPages").value(1))
            //.andExpect(jsonPath("$.totalElements").value(1))
            ;
            // Assert
            verify(userService, times(1)).findAll(pageable, criterioUser);
        }
    }

    @Nested
    class AlterarUser {
        @Test
        void devePermitirAlterarUser() throws Exception {
            // Arrange
            var user = UserHelper.getUser(true);
            when(userService.update(user.getId(), user)).thenAnswer(r -> r.getArgument(1) );
            // Act
            mockMvc.perform(put("/user/{id}", user.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(user)))
                    .andExpect(status().isAccepted());
            // Assert
            verify(userService, times(1)).update(user.getId(), user);
        }

        @Test
        void deveGerarExcecao_QuandoAlterarUser_RequisicaoXml() throws Exception {
            // Arrange
            var user = UserHelper.getUser(true);
            when(userService.update(user.getId(), user)).thenAnswer(r -> r.getArgument(1) );
            // Act
            mockMvc.perform(put("/user/{id}", user.getId())
                            .contentType(MediaType.APPLICATION_XML)
                            .content(asJsonString(user)))
                    .andExpect(status().isUnsupportedMediaType());
            // Assert
            verify(userService, never()).update(user.getId(), user);
        }

        @Test
        void deveGerarExcecao_QuandoAlterarUserPorId_idNaoExiste() throws Exception {
            // Arrange
            var userDTO = UserHelper.getUser(true);
            when(userService.update(userDTO.getId(), userDTO)).thenThrow(IllegalArgumentException.class);
            // Act
            mockMvc.perform(put("/user/{id}", userDTO.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(asJsonString(userDTO)))
                    .andExpect(status().isBadRequest());
            // Assert
            verify(userService, times(1)).update(any(UUID.class), any(User.class));
        }
    }

    @Nested
    class RemoverUser {
        @Test
        void devePermitirRemoverUser() throws Exception {
            // Arrange
            var user = UserHelper.getUser(true);
            doNothing().when(userService).delete(user.getId());
            when(securityHelper.getLoggedUser()).thenReturn(user.getLogin());
            // Act
            mockMvc.perform(delete("/user/{id}", user.getId()))
                    .andExpect(status().isNoContent());
            // Assert
            verify(userService, times(1)).delete(user.getId());
            verify(userService, times(1)).delete(user.getId());
        }

        @Test
        void deveGerarExcecao_QuandoRemoverUserPorId_idNaoExiste() throws Exception {
            // Arrange
            var user = UserHelper.getUser(true);
            when(securityHelper.getLoggedUser()).thenReturn(user.getLogin());
            doThrow(new IllegalArgumentException("User não encontrado com o ID: " + user.getId()))
                    .when(userService).delete(user.getId());
            // Act
            mockMvc.perform(delete("/user/{id}", user.getId()))
                    .andExpect(status().isBadRequest());
            // Assert
            verify(userService, times(1)).delete(user.getId());
        }
    }

    @Nested
    class LoginUser {
        @Test
        void devePermitirLoginUser() throws Exception {
            // Arrange
            var user = UserHelper.getUser(true);
            var token = new Token(UserHelper.getToken(user), null);
            when(userService.login(any(User.class))).thenReturn(token);
            // Act
            mockMvc.perform(
                            post(USER + "/login").contentType(MediaType.APPLICATION_JSON)
                                    .content(asJsonString(user)))
                    .andExpect(status().isCreated());
            // Assert
            verify(userService, times(1)).login(any(User.class));
        }
    }
}