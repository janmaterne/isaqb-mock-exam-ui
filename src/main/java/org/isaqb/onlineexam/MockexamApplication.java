package org.isaqb.onlineexam;

import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class MockexamApplication {

    @Value("${spring.h2.console.enabled}")
    private boolean h2ConsoleEnabled;



    public static void main(String[] args) {
		validateEnvironment();
		SpringApplication app = new SpringApplication(MockexamApplication.class);
		// Inject values into the banner.
		app.setDefaultProperties(Map.of(
			"app.version", BuildInfo.getVersion(),
			"app.buildtime", BuildInfo.getBuildTimestamp()
		));
		app.run(args);
	}

	private static void validateEnvironment() {
		if (!"UTF-8".equalsIgnoreCase(System.getProperty("file.encoding"))) {
			log.info("Die Anwendung muss mit -Dfile.encoding=UTF-8 gestartet werden.");
			System.exit(1);
		}
	}

	@PostConstruct
	public void logConsole() {
	    if (h2ConsoleEnabled) {
	        log.info("H2-Console available at http://localhost:8080/h2-console");
	    }
	}

}
