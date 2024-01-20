package org.isaqb.onlinetrainer.cli;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class GetConfigCliAction implements CommandLineRunner {

	private static final String CLI_NAME = "get-config";
	
	@Override
	public void run(String... args) throws Exception {
		if (Arrays.asList(args).contains(CLI_NAME)) {
			runAction();
		}
	}

	private void runAction() {
		ClassPathResource yaml = new ClassPathResource("application.yaml");
		File copiedTo = new File("application.yaml");
		try {
			Files.copy(yaml.getInputStream(), copiedTo.toPath());
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
	}

}
