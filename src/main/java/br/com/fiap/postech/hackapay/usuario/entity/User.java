package br.com.fiap.postech.hackapay.usuario.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "tb_user")
public class User {
    @Id
    @Column(name = "id", nullable = false)
    private UUID id;
    @Column(name = "login", nullable = false)
    @JsonProperty("usuario")
    private String login;
    @Column(name = "password", nullable = false)
    @JsonProperty("senha")
    private String password;

    public User() {
    }

    public User(String login, String password) {
        this();
        this.login = login;
        this.password = password;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
