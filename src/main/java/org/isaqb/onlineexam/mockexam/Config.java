package org.isaqb.onlineexam.mockexam;

import org.isaqb.onlineexam.mockexam.ui.AutloadJS;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

	@Bean
	public static AutloadJS create() {
		return springDevToolsPresent()
			? new AutloadJS(
				"<!-- Autoreload on local file change -->" +
				"<script type=\"text/javascript\" src=\"https://livejs.com/live.js\"></script>")
			: new AutloadJS("");
	}	
	
	private static boolean springDevToolsPresent() {
		try {
			Class.forName("org.springframework.boot.devtools.RemoteSpringApplication");
			return true;
		} catch (ClassNotFoundException e) {
			return false;
		}
	}
}
