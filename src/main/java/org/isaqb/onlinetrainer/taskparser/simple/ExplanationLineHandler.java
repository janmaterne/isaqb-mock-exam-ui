package org.isaqb.onlinetrainer.taskparser.simple;

import org.isaqb.onlinetrainer.model.Language;
import org.isaqb.onlinetrainer.model.Task;

class ExplanationLineHandler implements LineHandler {
	@Override
	public void handleLine(Task task, Language language, String line) {
		task.setExplanation(task.getExplanation() + line);
	}
}