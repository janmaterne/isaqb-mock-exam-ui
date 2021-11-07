package org.isaqb.onlineexam.mockexam;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.isaqb.onlineexam.mockexam.model.Language;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
// Map to all values of application.yaml
@ConfigurationProperties
@Data
public class QuizConfiguration {

	// Map key "quiz" according to its name.
	// Keys without any matching field are simply ignored.
	private Map<String, List<QuizConfig>> quiz;
	
	@Data
	static class QuizConfig {
		private Map<Language, String> name;
		// camelCase (urlTemplate) is mapped to snake-case (url-template)
		private String urlTemplate;
		private int from;
		private int to;
	}
	
	public Set<String> topics() {
		return quiz.keySet();
	}

}
