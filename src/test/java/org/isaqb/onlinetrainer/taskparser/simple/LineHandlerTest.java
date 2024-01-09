package org.isaqb.onlinetrainer.taskparser.simple;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

class LineHandlerTest {

	@Nested
	class OptionHandler {

		OptionLineHandler handler = new OptionLineHandler();
	
		@ParameterizedTest
		@MethodSource("selected")
		void isCorrect(String value) {
			assertTrue(handler.isCorrect(value));
		}

		@ParameterizedTest
		@MethodSource("notSelected")
		void isCorrect_selected_leading_X_in_value(String value) {
			assertFalse(handler.isCorrect(value));
		}

		@ParameterizedTest
		@MethodSource("selected")
		void text_selected(String value) {
			assertEquals("value", handler.text(value));
		}
			
		@ParameterizedTest
		@MethodSource("notSelected")
		void text_selected_leading_X_in_value(String value) {
			assertEquals("xvalue", handler.text(value).toLowerCase());
		}
		
		private static Stream<String> selected() {
			return Stream.of(
				"x  value", " x value", "x\tvalue", "\tx\tvalue",
				"X  value", " X value", "X\tvalue", "\tX\tvalue"
			);
		}
		
		private static Stream<String> notSelected() {
			return Stream.of(
				"  xvalue", "\txvalue", "\t\txvalue",
				"  Xvalue", "\tXvalue", "\t\tXvalue"
			);
		}
	}

}
