package org.isaqb.onlineexam.mockexam.loader;

import org.isaqb.onlineexam.mockexam.model.I18NText;
import org.isaqb.onlineexam.mockexam.model.Language;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class IntroductionLoader {

	private I18NText text = new I18NText();
	
	public IntroductionLoader(@Value("${url.introduction}") String url, StringLoader stringLoader, AsciidocReader reader) {
		String adoc = stringLoader.loadAsString(url).orElse("No content loaded");
		text = reader.parse(adoc);
	}

	public String getHtml(Language language) {
		return text.getText(language);
	}
	
}
