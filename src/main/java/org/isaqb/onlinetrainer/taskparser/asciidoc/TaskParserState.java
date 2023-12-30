package org.isaqb.onlinetrainer.taskparser.asciidoc;

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
