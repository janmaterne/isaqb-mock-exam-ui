package org.isaqb.onlinetrainer.taskparser.simple;

import org.isaqb.onlinetrainer.model.Language;
import org.isaqb.onlinetrainer.model.Task;
import org.isaqb.onlinetrainer.model.TaskType;

class HeaderLineHandler implements LineHandler {
	@Override
	public void handleLine(Task task, Language language, String line) {
		int splitPos = line.indexOf(":");
		String key = line.substring(0, splitPos).trim();
		String value = line.substring(splitPos+1).trim();
		switch (key) {
			case "id": {
				task.setId(value);
				break;
			}
			case "reachablePoints", "points": {
				task.setReachablePoints(Integer.parseInt(value));
				break;
			}
			case "type": {
				task.setType(TaskType.of(value));
				break;
			}
		}
	}
}