package org.isaqb.onlinetrainer.model;

import java.util.Locale;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum Language {

	EN(Locale.US),
	DE(Locale.GERMANY);
	
	@Getter
	private Locale locale;
	
	public static Language of(String... choices) {
		if (choices == null) {
			return EN;
		}
		for (String choice : choices) {
			if (choice != null) {
				try {
					return valueOf(choice);
				} catch (IllegalArgumentException e) {
					// ignore
				}
			}
		}
		return EN;
	}
}
