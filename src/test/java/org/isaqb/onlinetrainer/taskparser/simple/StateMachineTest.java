package org.isaqb.onlinetrainer.taskparser.simple;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StateMachineTest {

	@Test
	void isHeaderLine() {
		var sm = new StateMachine(null);
		assertFalse(sm.isHeaderLine(""));
		assertFalse(sm.isHeaderLine("word1"));
		assertFalse(sm.isHeaderLine("word1 :"));
		assertFalse(sm.isHeaderLine("word1 :  "));
		assertFalse(sm.isHeaderLine("word1 word2"));
		assertFalse(sm.isHeaderLine("word1 word2 : more text"));
		assertTrue(sm.isHeaderLine("key: value"));
		assertTrue(sm.isHeaderLine("key : value"));
		assertTrue(sm.isHeaderLine("key:value"));
		assertTrue(sm.isHeaderLine(" key: value"));
	}

}
