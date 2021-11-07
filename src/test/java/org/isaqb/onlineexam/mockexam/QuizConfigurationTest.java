package org.isaqb.onlineexam.mockexam;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.isaqb.onlineexam.mockexam.model.Language;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(QuizConfiguration.class)
public class QuizConfigurationTest {

	@Autowired
	private QuizConfiguration config;
	
	@Test
	public void yamlInjected() {
		assertNotNull(config);
		System.out.printf("%n%n%n%n%s%n%n%n%n%n", config);
	}
	
	@Test
	public void configQuizTopicBase() {
		var base = config.getQuiz().get("base").get(0);
		assertNotNull(base.getUrlTemplate());
		assertNotNull(base.getFrom());
		assertNotNull(base.getTo());
		assertNotNull(base.getName());
		assertNotNull(base.getName().get(Language.DE));
	}
	
}
