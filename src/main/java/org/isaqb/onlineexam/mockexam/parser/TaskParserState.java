package org.isaqb.onlineexam.mockexam.parser;

public enum TaskParserState {
	
	START,
	ID,
	TYPE,
	POINTS,
	TEXT,
	ANSWER_CORRECT,
	ANSWER_POSITION,
	ANSWER_TEXT;
	
}
