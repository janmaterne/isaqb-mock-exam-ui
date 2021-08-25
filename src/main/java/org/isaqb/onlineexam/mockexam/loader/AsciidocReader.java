package org.isaqb.onlineexam.mockexam.loader;

import org.asciidoctor.Asciidoctor;
import org.asciidoctor.Options;
import org.isaqb.onlineexam.mockexam.model.I18NText;
import org.isaqb.onlineexam.mockexam.model.Language;
import org.springframework.stereotype.Component;

@Component
public class AsciidocReader {
	
	Asciidoctor doctor = Asciidoctor.Factory.create();

	public I18NText parse(String adoc) {
		I18NText text = new I18NText();
		
		for(Language lang : Language.values()) {
			String languageSpecificADoc = filter(adoc, lang);
			String html = doctor.convert(languageSpecificADoc, Options.builder()
					.backend("html5")
					.compact(true)
					.toFile(false)
					.build());
			text.addText(lang, html);
		}
		
		return text;
	}

	/**
	 * Filter the complete asciidoc document by its tags.
	 * @param adoc
	 * @param language
	 * @return
	 */
	protected String filter(String adoc, Language language) {
		String startPattern = "//\\s+tag::" + language.toString() + "\\[.*";
		String endPattern = "//\\s+end::" + language.toString() + ".*";
		StringBuilder rv = new StringBuilder();
		
		boolean inLanguageBlock = false;
		for(String line : adoc.split("\n")) {
			if (line.trim().matches(startPattern)) {
				inLanguageBlock = true;
			}
			if (line.trim().matches(endPattern)) {
				inLanguageBlock = false;
			}
			if (inLanguageBlock) {
				rv.append(line).append(System.lineSeparator());
			}
		}
		
		return rv.toString();
	}
}
