package org.isaqb.onlinetrainer.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class LanguageTest {

	@Test
	void of() {
		assertEquals(Language.DE, Language.of("DE"));
		assertEquals(Language.EN, Language.of("EN"));
		assertEquals(Language.DE, Language.of("DE", "EN"));
		assertEquals(Language.DE, Language.of(null, "DE"));
		assertEquals(Language.EN, Language.of( (String[]) null));
		assertEquals(Language.EN, Language.of("not-supported"));
		assertEquals(Language.DE, Language.of(null, "not-supported", "DE"));
	}

}
