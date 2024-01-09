package org.isaqb.onlinetrainer.taskparser.simple;

import java.util.Optional;
import java.util.regex.Pattern;

import org.isaqb.onlinetrainer.model.I18NText;
import org.isaqb.onlinetrainer.model.Language;
import org.isaqb.onlinetrainer.model.Option;
import org.isaqb.onlinetrainer.model.Task;

class OptionLineHandler implements LineHandler {
	
	/**
	 * Pattern for getting selected lines:
	 * <ul>
	 *   <li>optional an arbitraty number of whitespaces
	 *   <li>selection character 'x' or 'X'
	 *   <li>minimum of 1 whitespace
	 *   <li>the value
	 * </ul>
	 */
	private static Pattern selectedPattern = Pattern.compile("^\\s*[xX]\\s+.*");
	
	private char position = 'a';

	@Override
	public void handleLine(Task task, Language language, String line) {
		if (line.isBlank()) {
			return;
		}
		char nextPosition = nextPosition(task);
		Optional<Option> opt = task.findOptionByPosition(nextPosition);
		if (opt.isEmpty()) {
			// first language: we have to create the options
			task.addPossibleOption(new Option(nextPosition, isCorrect(line), text(language, line)));
		} else {
			// next language: we have to update the option
			opt.get().getText().addText(language, text(line));
		}
	}
	
	protected char nextPosition(Task task) {
		// nice, that we could increase the char ;-)
		return position++;
	}

	protected boolean isCorrect(String line) {
		return selectedPattern.matcher(line).matches();
	}

	protected I18NText text(Language language, String line) {
		var text = new I18NText();
		text.addText(language, text(line));
		return text;
	}

	protected String text(String line) {
		var value = isCorrect(line) ? line.trim().substring(1).trim() : line.trim();
		return value;
	}
}