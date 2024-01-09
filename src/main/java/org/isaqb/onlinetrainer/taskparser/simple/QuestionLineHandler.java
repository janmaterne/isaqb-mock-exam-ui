package org.isaqb.onlinetrainer.taskparser.simple;

import org.isaqb.onlinetrainer.model.I18NText;
import org.isaqb.onlinetrainer.model.Language;
import org.isaqb.onlinetrainer.model.Task;

class QuestionLineHandler implements LineHandler {
	@Override
	public void handleLine(Task task, Language language, String line) {
		if (line.isBlank()) {
			return;
		}
		if (task.getQuestion() == null) {
			task.setQuestion(new I18NText());
		}
		task.getQuestion().addText(language, line.trim());
	}
}