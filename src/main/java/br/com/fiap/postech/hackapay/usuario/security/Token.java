package br.com.fiap.postech.hackapay.usuario.security;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record Token(
        String accessToken,

        String erro
) {
}
