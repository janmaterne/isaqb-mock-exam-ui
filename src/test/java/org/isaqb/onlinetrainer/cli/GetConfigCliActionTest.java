package org.isaqb.onlinetrainer.cli;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(args = "get-config")
class GetConfigCliActionTest {

	File copied = new File("application.yaml");
	
	@AfterEach
	void clean() {
		copied.delete();
	}
	
	@Test
	void test() throws IOException {
		assertTrue(copied.exists());
		String content = Files.readString(copied.toPath());
		assertTrue(content.contains("# Where is the source of the introduction document?"));
		assertTrue(content.contains("max-numbers-of-questions: 5"));
		assertTrue(content.contains("server.servlet.encoding.enabled: true"));
		assertFalse(content.contains("\t"));
	}

}
