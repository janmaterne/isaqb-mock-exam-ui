package org.isaqb.onlinetrainer.taskparser.simple;

import org.isaqb.onlinetrainer.model.Language;
import org.isaqb.onlinetrainer.model.Task;

interface LineHandler {
	void handleLine(Task task, Language language, String line);
}