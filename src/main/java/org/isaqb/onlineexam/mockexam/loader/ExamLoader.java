package org.isaqb.onlineexam.mockexam.loader;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.isaqb.onlineexam.mockexam.model.Exam;
import org.isaqb.onlineexam.mockexam.model.Task;
import org.isaqb.onlineexam.mockexam.model.TaskValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

/**
 * Load the whole exam with all questions and their options.
 */
@Component
@AllArgsConstructor
public class ExamLoader {

	@Autowired
	private TaskValidator validator;
	
	@Bean
	public Exam loadExam(TaskLoader taskLoader, @Value("${exam.requiredPoints}") double requiredPoints) {
		List<Task> tasks = taskLoader.loadTasks();
		validate(tasks);
		return new Exam(requiredPoints, tasks);
	}

	private void validate(List<Task> tasks) {
		String errorMsg = tasks.stream()
			.map(this::validate)
			.filter(Optional::isPresent)
			.map(Optional::get)
			.collect(Collectors.joining("\n"));
		if (errorMsg.length() > 0) {
			System.out.println("Fehler bei den geladenen Tasks:");
			System.out.println(errorMsg);
		}
	}
	
	private Optional<String> validate(Task task) {
		var errors = validator.validate(task);
		if (!errors.isEmpty()) {
			return Optional.of(task + "\n- " + String.join("\n- ", errors) + "\n");
		} else {
			return Optional.empty();
		}
	}
	
}
