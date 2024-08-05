package br.com.fiap.postech.hackapay.usuario.controller;

import br.com.fiap.postech.hackapay.security.UserDetailsServiceImpl;
import br.com.fiap.postech.hackapay.usuario.entity.User;
import br.com.fiap.postech.hackapay.usuario.helper.UserHelper;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
@ActiveProfiles("test")
public class UserControllerIT {

    public static final String USER = "/api/usuario";
    @LocalServerPort
    private int port;

    @MockBean
    private UserDetailsServiceImpl userDetailsService;

    @BeforeEach
    void setup() {
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @Nested
    class CadastrarUser {
        @Test
        void devePermitirCadastrarUser() {
            var user = UserHelper.getUser(false);
            user.setLogin(user.getLogin() + "!!!");
            var userDetails = UserHelper.getUserDetails(user);
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
            given()
                .contentType(MediaType.APPLICATION_JSON_VALUE).body(user)
                .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(user))
            .when()
                .post(USER)
            .then()
                .statusCode(HttpStatus.CREATED.value())
                .body(matchesJsonSchemaInClasspath("schemas/user.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoCadastrarUser_RequisicaoXml() {
            given()
                .contentType(MediaType.APPLICATION_XML_VALUE)
            .when()
                .post(USER)
            .then()
                .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value())
                .body(matchesJsonSchemaInClasspath("schemas/error.schema.json"));
        }
    }

    @Nested
    class BuscarUser {
        @Test
        void devePermitirBuscarUserPorId() {
            var id = UUID.fromString("7a04f6fb-c79b-4b47-af54-9bef34cbab35");
            var user = UserHelper.getUser(false);
            user.setId(id);
            var userDetails = UserHelper.getUserDetails(user);
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
            given()
                .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(user))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get(USER + "/{id}", id)
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/user.schema.json"));
        }
        @Test
        void devePermitirBuscarUserPorLogin() {
            var login = "anderson.wagner@gmail.com";
            given()
                    //.header(HttpHeaders.AUTHORIZATION, UserHelper.getToken())
                    .contentType(MediaType.APPLICATION_JSON_VALUE)
                    .when()
                    .get(USER + "/findByLogin/{login}", login)
                    .then()
                    .statusCode(HttpStatus.OK.value())
                    .body(matchesJsonSchemaInClasspath("schemas/user.schema.json"));
        }
        @Test
        void deveGerarExcecao_QuandoBuscarUserPorId_idNaoExiste() {
            var user = UserHelper.getUser(true);
            var userDetails = UserHelper.getUserDetails(user);
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
            given()
                .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(user))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get(USER + "/{id}", user.getId())
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value());
        }

        @Test
        void devePermitirBuscarTodosUser() {
            User user = UserHelper.getUser(true);
            var userDetails = UserHelper.getUserDetails(user);
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
            given()
                .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(user))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get(USER)
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/user.page.schema.json"));
        }

        @Test
        void devePermitirBuscarTodosUser_ComPaginacao() {
            User user = UserHelper.getUser(true);
            var userDetails = UserHelper.getUserDetails(user);
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
            given()
                .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(user))
                .queryParam("page", "1")
                .queryParam("size", "1")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .get(USER)
            .then()
                .statusCode(HttpStatus.OK.value())
                .body(matchesJsonSchemaInClasspath("schemas/user.page.schema.json"));
        }
    }

    @Nested
    class AlterarUser {
        @Test
        void devePermitirAlterarUser(){
            var user = new User(
                    "kaiby.santos@gmail.com",
                    "uma nova senha"
            );
            user.setId(UUID.fromString("c5ce37f4-3160-48d0-bd89-1d680ff77808"));
            var userDetails = UserHelper.getUserDetails(user);
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
            given()
                .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(user))
                .body(user).contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .put(USER + "/{id}", user.getId())
            .then()
                .statusCode(HttpStatus.ACCEPTED.value())
                .body(matchesJsonSchemaInClasspath("schemas/user.schema.json"));
        }

        @Test
        void deveGerarExcecao_QuandoAlterarUser_RequisicaoXml() {
            var user = UserHelper.getUser(true);
            var userDetails = UserHelper.getUserDetails(user);
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
            given()
                .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(user))
                .body(user).contentType(MediaType.APPLICATION_XML_VALUE)
            .when()
                .put(USER + "/{id}", user.getId())
            .then()
                .statusCode(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
        }

        @Test
        void deveGerarExcecao_QuandoAlterarUserPorId_idNaoExiste() {
            var user = UserHelper.getUser(true);
            var userDetails = UserHelper.getUserDetails(user);
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
            given()
                .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(user))
                .body(user).contentType(MediaType.APPLICATION_JSON_VALUE)
            .when()
                .put(USER + "/{id}", user.getId())
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("User não encontrado com o ID: " + user.getId()));
        }
    }

    @Nested
    class RemoverUser {
        @Test
        void devePermitirRemoverUser() {
            var user = UserHelper.getUser(false);
            user.setId(UUID.fromString("f6497965-3cf0-4601-a631-01878ef70f40"));
            var userDetails = UserHelper.getUserDetails(user);
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
            given()
                .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(user))
            .when()
                .delete(USER + "/{id}", user.getId())
            .then()
                .statusCode(HttpStatus.NO_CONTENT.value());
        }

        @Test
        void deveGerarExcecao_QuandoRemoverUserPorId_idNaoExiste() {
            var user = UserHelper.getUser(true);
            var userDetails = UserHelper.getUserDetails(user);
            when(userDetailsService.loadUserByUsername(anyString())).thenReturn(userDetails);
            given()
                .header(HttpHeaders.AUTHORIZATION, UserHelper.getToken(user))
            .when()
                .delete(USER + "/{id}", user.getId())
            .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .body(equalTo("User não encontrado com o ID: " + user.getId()));
        }
    }
}
