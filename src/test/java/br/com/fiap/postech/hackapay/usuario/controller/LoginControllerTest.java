package br.com.fiap.postech.hackapay.usuario.controller;

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
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LoginControllerTest {
    public static final String AUTENTICACAO = "/autenticacao";
    private MockMvc mockMvc;
    @Mock
    private UserService userService;

    private AutoCloseable mock;

    @BeforeEach
    void setUp() {

        mock = MockitoAnnotations.openMocks(this);
        LoginController loginController = new LoginController(userService);
        mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        mock.close();
    }

    public static String asJsonString(final Object object) throws Exception {
        return new ObjectMapper().writeValueAsString(object);
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
                            post(AUTENTICACAO).contentType(MediaType.APPLICATION_JSON)
                                    .content(asJsonString(user)))
                    .andExpect(status().isCreated());
            // Assert
            verify(userService, times(1)).login(any(User.class));
        }
    }
}