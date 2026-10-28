package meci_e_services;

import meci_e_services.entity.AppUsers;
import meci_e_services.repository.AppUsersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {

	private static final Logger log =
			LoggerFactory.getLogger(StartupRunner.class);

	@Override
	public void run(String... args) {
		log.info("========================================");
		log.info("🚀 MECI E-SERVICES BACKEND DEMARRÉ");
		log.info("🌍 URL : http://localhost:8080");
		log.info("📡 API : http://localhost:8080/api");
		log.info("========================================");
	}
}
