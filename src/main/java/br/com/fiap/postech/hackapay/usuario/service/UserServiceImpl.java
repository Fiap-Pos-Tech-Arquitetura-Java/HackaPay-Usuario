package br.com.fiap.postech.hackapay.usuario.service;

import br.com.fiap.postech.hackapay.security.JwtService;
import br.com.fiap.postech.hackapay.usuario.entity.User;
import br.com.fiap.postech.hackapay.usuario.repository.UserRepository;
import br.com.fiap.postech.hackapay.usuario.security.Token;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, JwtService jwtService) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
    }

    @Override
    public User save(User user) {
        if (userRepository.findByLogin(user.getLogin()).isPresent()) {
            throw new IllegalArgumentException("Já existe um user cadastrado com esse login.");
        }
        user.setId(UUID.randomUUID());
        user.setPassword(getEncryptedPassword(user.getPassword()));
        return userRepository.save(user);
    }

    private String getEncryptedPassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }

    @Override
    public Page<User> findAll(Pageable pageable, User user) {
        Example<User> userExample = Example.of(user);
        return userRepository.findAll(userExample, pageable);
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User não encontrado com o ID: " + id));
    }

    @Override
    public User update(UUID id, User userParam) {
        User user = findById(id);
        if (userParam.getId() != null && !user.getId().equals(userParam.getId())) {
            throw new IllegalArgumentException("Não é possível alterar o id de um user.");
        }
        if (userParam.getLogin() != null && !user.getLogin().equals(userParam.getLogin())) {
            throw new IllegalArgumentException("Não é possível alterar o login de um user.");
        }
        if (StringUtils.isNotEmpty(userParam.getPassword())) {
            user.setPassword(getEncryptedPassword(user.getPassword()));
        }
        user = userRepository.save(user);
        return user;
    }

    @Override
    public void delete(UUID id) {
        findById(id);
        userRepository.deleteById(id);
    }

    @Override
    public User findByLogin(String username) {
        return userRepository.findByLogin(username)
                .orElseThrow(() -> new IllegalArgumentException("User não encontrado com o login: " + username));
    }

    @Override
    public Token login(User user) throws Exception {
        Optional<User> optionalUsuario = userRepository.findByLogin(user.getLogin());
        if (optionalUsuario.isEmpty()) {
            throw new Exception("Usuario informado não encontrado.");
        }
        User u = optionalUsuario.get();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(user.getPassword(), u.getPassword())) {
            throw new Exception("Senha do User informado não confere.");
        }
        br.com.fiap.postech.hackapay.security.User userSecurity =
                new br.com.fiap.postech.hackapay.security.User(user.getLogin(), user.getPassword());
        return new Token(jwtService.generateToken(userSecurity), null);
    }
}
