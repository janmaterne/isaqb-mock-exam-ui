package org.isaqb.onlinetrainer.taskparser.simple;

import java.util.regex.Pattern;

import org.isaqb.onlinetrainer.model.Language;
import org.isaqb.onlinetrainer.model.Task;

import lombok.Getter;

class StateMachine {
	
	
	/**
	 * Which state does the statemachine have?
	 * Means: where in the file am I?
	 */
	enum State {
		
		HEADER, QUESTION, OPTIONS, EXPLANATION;
		
		/**
		 * @return the next usual state
		 */
		public State next() {
			return switch (this) {
				case HEADER -> QUESTION;
				case QUESTION -> OPTIONS;
				case OPTIONS -> EXPLANATION;
				case EXPLANATION -> EXPLANATION;
			};
		}
	}
	
	
	
	/** 
	 * Marker line for changing the language: 
	 * <tt>===DE===</tt>, <tt>===EN===</tt>
	 * More '=' signs are possible (min=3). 
	 */
	private Pattern languageLine = Pattern.compile("\\*{3,}(..)\\*{3,}");
	/** 
	 * Marker line for changing the state/area: <tt>---</tt>
	 * More '-' signs are possible (min=3). 
	 */
	private Pattern changeLine = Pattern.compile("\\-{3,}");
	
	
	@Getter
	private Task task;
	private Language currentLanguage = Language.DE;
	private State currentState = State.HEADER;
	
	private LineHandler handler;
	
	
	public StateMachine(Task task) {
		this.task = task;
	}

	
	public void processLine(String line) {
		maybeInitHandler(line);
		var matcher = languageLine.matcher(line);
		if (matcher.matches()) {
			var value = matcher.group(1);
			currentLanguage = Language.valueOf(value);
			currentState = State.QUESTION;
			handler = handler();
			return;
		}
		if (changeLine.matcher(line.trim()).matches()) {
			currentState = currentState.next();
			handler = handler();
			return;
		}
		handler.handleLine(task, currentLanguage, line);
	}

	private void maybeInitHandler(String line) {
		if (handler == null) {
			// Difference between full format and format with defaults (= without header).
			// ':' means, this is a header instruction like 'id: my-id'
			currentState = line.contains(":") ? State.HEADER : State.QUESTION;
			handler = handler();
		}
	}

	private LineHandler handler() {
		return switch (currentState) {
			case HEADER -> new HeaderLineHandler();
			case QUESTION -> new QuestionLineHandler();
			case OPTIONS -> new OptionLineHandler();
			case EXPLANATION -> new ExplanationLineHandler();
		};
	}
}