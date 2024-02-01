package org.isaqb.onlinetrainer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.isaqb.onlinetrainer.model.Language;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class Messages {
	
	private Map<Language, Properties> msg = new HashMap<Language, Properties>();
	
	@PostConstruct
	private void init() {
		for(var lang : Language.values()) {
			try {
				var props = new Properties();
				var res = new ClassPathResource("messages_" + lang.name().toLowerCase() + ".properties");
				props.load(new InputStreamReader(res.getInputStream(), Charset.forName("UTF-8")));
				msg.put(lang, props);
			} catch (IOException e) {
				log.error(e.getMessage(), e);
			}
		}
	}
	
	public String getMessage(Language language, String key) {
		return msg.get(language).getProperty(key);
	}
	
}
