package org.isaqb.onlinetrainer;

import static org.assertj.core.api.Assertions.assertThat;

import org.isaqb.onlinetrainer.model.Language;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = Messages.class)
class MessagesTest {

	@Autowired
	private Messages messages;
	
	@Test
	void quizhintDE() {
		assertThat(messages.getMessage(Language.DE, "quizhint")).contains("zukünftig");
	}

	@Test
	void quizhintEN() {
		assertThat(messages.getMessage(Language.EN, "quizhint")).contains("future");
	}

}
