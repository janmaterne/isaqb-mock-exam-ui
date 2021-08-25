package org.isaqb.onlineexam.mockexam.model;

import java.util.HashMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@ToString
public class I18NText {
	
	private Map<Language, String> text = new HashMap<>();
	
	public void addText(Language language, String text) {
		if (this.text.containsKey(language)) {
			String newText = this.text.get(language) + System.lineSeparator() + text;
			this.text.put(language, newText);
		} else {
			this.text.put(language, text);
		}
	}
	
	public String getText(Language language) {
		return this.text.get(language);
	}
	
	public Map<Language, String> getMap() {
		return text;
	}
	
	public boolean isEmpty() {
		return this.text.isEmpty();
	}

}
