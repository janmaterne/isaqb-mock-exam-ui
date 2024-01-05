package org.isaqb.onlinetrainer.loader;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.isaqb.onlinetrainer.model.I18NText;
import org.isaqb.onlinetrainer.model.Language;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class AsciidocReaderTest {

	@Autowired
	private AsciidocReader adocReader = new AsciidocReader();

	private static String adoc;

    @BeforeAll
    static void readAsciiDoc() throws IOException {
		Path data = Path.of("src/main/resources", "messages", "cookie-disclaimer.adoc");
		adoc = Files.readString(data);
	}

    @Test
    void filterDE() {
		var adocDE = adocReader.filter(adoc, Language.DE);
		assertTrue(adocDE.contains("Es werden keinerlei sonstige Cookies verwendet."));
	}

    @Test
    void parse() throws IOException {
		I18NText text = adocReader.parse(adoc);
		assertTrue(text.getText(Language.EN).contains("Cookie-Disclaimer</h2>"));
		assertTrue(text.getText(Language.EN).contains("end of the exam.</p>"));
		assertTrue(text.getText(Language.DE).contains("Cookie-Disclaimer</h2>"));
		assertTrue(text.getText(Language.DE).contains("Durchgangs gelöscht.</p>"));
	}
	
}
