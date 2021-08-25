package org.isaqb.onlineexam.mockexam.util;

import java.util.HashMap;
import java.util.Map;

import org.isaqb.onlineexam.mockexam.model.Language;

public class LanguageData<K,V> {

	private Map<Language, Map<K,V>> data = new HashMap<>();
	
	public LanguageData<K, V> put(Language language, K key, V value) {
		data.computeIfAbsent(language, lang -> new HashMap<K, V>())
		    .put(key, value);
		return this;
	}
	
	public V get(Language language, K key) {
		return data.get(language).get(key);
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(getClass().getName()).append(System.lineSeparator());
		data.keySet().forEach( lang -> sb.append(lang).append(": ").append(data.get(lang)).append(System.lineSeparator()) );
		return sb.toString();
	}
	
}
