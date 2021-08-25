package org.isaqb.onlineexam.mockexam.model.loader;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.isaqb.onlineexam.mockexam.ApplicationProperties;
import org.isaqb.onlineexam.mockexam.loader.ExamLoader;
import org.isaqb.onlineexam.mockexam.loader.StringLoader;
import org.isaqb.onlineexam.mockexam.loader.TaskLoader;
import org.isaqb.onlineexam.mockexam.model.Exam;
import org.isaqb.onlineexam.mockexam.parser.TaskParser;
import org.junit.jupiter.api.Test;

public class ExamLoaderTest {

	ApplicationProperties appProps = new ApplicationProperties();
	
	@Test
	public void examInitialized() {
		var exam = loadExamWithOneTask();
		assertNotNull(exam);
		assertFalse(exam.getTasks().isEmpty());
		assertFalse(exam.getTasks().get(0).getPossibleOptions().isEmpty());
	}

	private Exam loadExamWithOneTask() {
		StringLoader stringLoader = new StringLoader(appProps.urlTemplate(), 1, 1);
		TaskLoader taskLoader = new TaskLoader(new TaskParser(), stringLoader);
		return new ExamLoader().loadExam(taskLoader, 15);
	}
	
}
