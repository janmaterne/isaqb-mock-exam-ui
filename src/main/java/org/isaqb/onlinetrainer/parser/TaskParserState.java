package org.isaqb.onlinetrainer.parser;

public enum TaskParserState {

	START,
	ID,
	TYPE,
	POINTS,
	TEXT,
	ANSWER_CORRECT,
	ANSWER_POSITION,
	ANSWER_TEXT,
	EXPLANATION;

}
