package org.isaqb.onlineexam.mockexam.loader;

import org.isaqb.onlineexam.mockexam.model.Exam;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Load the whole exam with all questions and their options.
 */
@Component
public class ExamLoader {

	@Bean
	public Exam loadExam(TaskLoader taskLoader, @Value("${exam.requiredPoints}") double requiredPoints) {
		return new Exam(requiredPoints, taskLoader.loadTasks());
	}
	
}
