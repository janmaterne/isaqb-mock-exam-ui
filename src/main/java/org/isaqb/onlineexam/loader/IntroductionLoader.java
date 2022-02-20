package org.isaqb.onlineexam.loader;

import org.isaqb.onlineexam.model.I18NText;
import org.isaqb.onlineexam.model.Language;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IntroductionLoader {

	private I18NText text = new I18NText();
	
	public IntroductionLoader(@Value("${introduction}") String url, Loader loader, AsciidocReader reader) {
		String adoc = loader.loadAsString(url).orElse("No content loaded");
		text = reader.parse(adoc);
	}

	public String getHtml(Language language) {
		return text.getText(language);
	}
	
}
