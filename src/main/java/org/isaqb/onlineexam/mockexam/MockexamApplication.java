package org.isaqb.onlineexam.mockexam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MockexamApplication {

	public static void main(String[] args) {
		verify();
		SpringApplication.run(MockexamApplication.class, args);
	}

	private static void verify() {
		if (!"UTF-8".equalsIgnoreCase(System.getProperty("file.encoding"))) {
			System.out.println("Die Anwendung muss mit -Dfile.encoding=UTF-8 gestartet werden.");
			System.exit(1);
		}
	}

}
