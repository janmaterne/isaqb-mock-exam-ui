package org.isaqb.onlinetrainer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
public class XTest {
	
	@Nested
	@TestPropertySource(locations = "classpath:x.properties")
	public class XNested {
		@Autowired
		@Value("${quizmode.max-numbers-of-questions}")
		int x;
		@Test
		public void x() {
			System.out.println("Num of Questions: " + x);
			assertEquals(42, x);
		}
	}

	@Nested
	@TestPropertySource(locations = "classpath:x2.properties")
	public class YNested {
		@Autowired
		@Value("${quizmode.max-numbers-of-questions}")
		int x;
		@Test
		public void x() {
			System.out.println("Num of Questions: " + x);
			assertEquals(49, x);
		}
	}
}
