package br.com.fiap.postech.hackapay.usuario;

import br.com.fiap.postech.hackapay.usuario.entity.User;
import br.com.fiap.postech.hackapay.usuario.repository.UserRepository;
import br.com.fiap.postech.hackapay.usuario.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HackaPayUsuarioApplication {

	public static void main(String[] args) {
		SpringApplication.run(HackaPayUsuarioApplication.class, args);
	}

	@Bean
	public CommandLineRunner demoData(UserService service) {
		return args -> {
			service.save(new User("adj2", "adj@1234"));
		};
	}
}
