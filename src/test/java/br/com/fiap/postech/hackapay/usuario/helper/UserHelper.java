package br.com.fiap.postech.hackapay.usuario.helper;

import br.com.fiap.postech.hackapay.security.JwtService;
import br.com.fiap.postech.hackapay.security.UserDetailsImpl;
import br.com.fiap.postech.hackapay.usuario.entity.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

public class UserHelper {
    public static User getUser(boolean geraId) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        var user = new User(
                "anderson.wagner@gmail.com",
                encoder.encode("123456")
        );
        if (geraId) {
            user.setId(UUID.randomUUID());
        }
        return user;
    }

    public static String getToken() {
        return getToken(getUser(true));
    }

    public static String getToken(User user) {
        br.com.fiap.postech.hackapay.security.User userSecurity = getUserForSecurity(user);
        return "Bearer " + new JwtService().generateToken(userSecurity);
    }

    public static UserDetails getUserDetails(User user) {
        return new UserDetailsImpl(getUserForSecurity(user));
    }

    private static br.com.fiap.postech.hackapay.security.User getUserForSecurity(User user) {
        br.com.fiap.postech.hackapay.security.User userSecurity =
                new br.com.fiap.postech.hackapay.security.User(user.getLogin(), user.getPassword());
        return userSecurity;
    }
}
