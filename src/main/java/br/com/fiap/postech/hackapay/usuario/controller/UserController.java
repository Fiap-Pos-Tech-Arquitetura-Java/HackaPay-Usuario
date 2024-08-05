package br.com.fiap.postech.hackapay.usuario.controller;

import br.com.fiap.postech.hackapay.usuario.entity.User;
import br.com.fiap.postech.hackapay.usuario.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Service
@RequestMapping("/usuario")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "registra um user")
    @PostMapping
    public ResponseEntity<User> save(@Valid @RequestBody User userDTO) {
        User savedUserDTO = userService.save(userDTO);
        return new ResponseEntity<>(savedUserDTO, HttpStatus.CREATED);
    }

    @Operation(summary = "lista todos os users")
    @GetMapping(
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Page<User>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String usuario
    ) {
        User user = new User(usuario, null);
        user.setId(null);
        var pageable = PageRequest.of(page, size);
        var users = userService.findAll(pageable, user);
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @Operation(summary = "lista um user por seu id")
    @GetMapping("/{id}")
    public ResponseEntity<?> findById(@PathVariable UUID id) {
        try {
            User user = userService.findById(id);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "lista um user por seu login", hidden = true)
    @GetMapping("/findByLogin/{login}")
    public ResponseEntity<User> findByLogin(@PathVariable String login) {
        try {
            User user = userService.findByLogin(login);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "altera um user por seu id")
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id, @Valid @RequestBody User userDTO) {
        try {
            User updatedUser = userService.update(id, userDTO);
            return new ResponseEntity<>(updatedUser, HttpStatus.ACCEPTED);
        } catch (IllegalArgumentException exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @Operation(summary = "remove um user por seu id")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        try {
            userService.delete(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException
                exception) {
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
