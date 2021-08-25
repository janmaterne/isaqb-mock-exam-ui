package org.isaqb.onlineexam.mockexam.loader;

import java.util.List;
import java.util.Optional;

import org.isaqb.onlineexam.mockexam.model.Task;
import org.isaqb.onlineexam.mockexam.parser.TaskParser;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

/**
 * Loads a list of {@link Task}s by using the given
 * {@link TaskParser} and {@link StringLoadero}.
 */
@AllArgsConstructor
@Component
public class TaskLoader {

	private TaskParser parser;
	private StringLoader loader;
	
	public List<Task> loadTasks() {
		return loader.remoteUrls().stream()
			.map(loader::loadAsString)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.map(parser::parseADoc)
			.toList();
	}
	
}
