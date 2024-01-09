package org.isaqb.onlinetrainer.taskparser.simple;

import org.isaqb.onlinetrainer.model.Task;
import org.isaqb.onlinetrainer.model.TaskType;
import org.isaqb.onlinetrainer.taskparser.TaskParser;

/**
 * Parses "Simple Text Format":
 * <pre>
 * HEADER
 * ===LANGUAGE===
 * QUESTION
 * ---
 * OPTIONS
 * ---
 * EXPLANATION
 * ===NEXT LANGUAGE===
 * ...
 * </pre>
 * 
 * or more simple (with using defaults for header and language=DE):
 * <pre>
 * QUESTION
 * ---
 * OPTIONS
 * ---
 * EXPLANATION (optional) 
 * </pre>
 *  
 */
public class SimpleTaskParser implements TaskParser {

	@Override
	public Task parseContent(String content) {
		var statemachine = new StateMachine(defaultTask(content));
		content.lines().forEach(statemachine::processLine);
		return statemachine.getTask();
	}

	private Task defaultTask(String content) {
		Task task = new Task();
		task.setId("hash-" + content.hashCode());
		task.setReachablePoints(1);
		task.setType(TaskType.PICK_FROM_MANY);
		return task;
	}
	
}
