package org.isaqb.onlineexam.mockexam.model.loader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import org.isaqb.onlineexam.mockexam.ApplicationProperties;
import org.isaqb.onlineexam.mockexam.loader.StringLoader;
import org.isaqb.onlineexam.mockexam.loader.TaskLoader;
import org.isaqb.onlineexam.mockexam.model.Language;
import org.isaqb.onlineexam.mockexam.model.Option;
import org.isaqb.onlineexam.mockexam.model.Task;
import org.isaqb.onlineexam.mockexam.parser.TaskParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

public class TaskLoaderTest {

	ApplicationProperties appProps = new ApplicationProperties();

	@Test
	public void remoteUrls() {
		StringLoader loader = new StringLoader("http://question-{NR}.adoc", 9, 11);
		List<String> remoteUrls = loader.remoteUrls();
		assertEquals(3, remoteUrls.size());
		assertTrue(remoteUrls.contains("http://question-09.adoc"));
		assertTrue(remoteUrls.contains("http://question-10.adoc"));
		assertTrue(remoteUrls.contains("http://question-11.adoc"));
	}
	
	@Test
	public void loadAsString() {
		String remoteUrl = "https://raw.githubusercontent.com/isaqb-org/examination-foundation/master/raw/mock_exam/docs/questions/question-01.adoc";
		Optional<String> opt = new StringLoader(null, 0, 0).loadAsString(remoteUrl);
		assertTrue(opt.isPresent());
		assertTrue(opt.get().contains("Q-20-04-01"));
	}
	
	@Test
	public void loadTasks() {
		TaskLoader taskLoader = new TaskLoader(
			new TaskParser(),
			new StringLoader(appProps.urlTemplate(), 1, 3)
		);
		var tasks = taskLoader.loadTasks();
		assertEquals(3, tasks.size());
	}
	
	

	@ParameterizedTest
	@MethodSource("questionNumberGenerator")
	public void queckRemoteDefinedTasksIsValid(int questionNr) {
		TaskLoader taskLoader = new TaskLoader(
			new TaskParser(),
			new StringLoader(appProps.urlTemplate(), questionNr, questionNr)
		);
		var tasks = taskLoader.loadTasks();
		var error = validate(tasks.get(0));
		assertNull(error);
	}
	
	private static IntStream questionNumberGenerator() {
		return IntStream.rangeClosed(1, 39);
	}
	
	
	
	private String validate(Task task) {
		StringBuilder sb = new StringBuilder();
		if (isNullOrBlank(task.getId())) {
			sb.append("- 'id' is empty\n");
		}
		if (task.getType() == null) {
			sb.append("- 'type' is not set\n");
		}
		if (task.getReachablePoints() <= 0) {
			sb.append("- no reachable points (")
			  .append(task.getReachablePoints())
			  .append(")\n");
		}
		if (task.getQuestion() == null) {
			sb.append("- no question present\n");
		} else {
			if (isNullOrBlank(task.getQuestion().getText(Language.DE))) {
				sb.append("- DE-question missing\n");
			}
			if (isNullOrBlank(task.getQuestion().getText(Language.EN))) {
				sb.append("- EN-question missing\n");
			}
		}
		if (task.getPossibleOptions().isEmpty()) {
			sb.append("- no answers present\n");
		} else {
			if (noCorrectOptionPresent(task.getPossibleOptions())) {
				sb.append("- no answer marked as correct\n");
			}
			for(Option answer : task.getPossibleOptions()) {
				if (answer.getPosition() < 'a' || answer.getPosition() > 'z') {
					sb.append("- found answer with illegal position: ").append(answer).append(System.lineSeparator());
				}
			}
		}
		
		if (sb.isEmpty()) {
			return null;
		} else {
			return task + System.lineSeparator() + sb.toString();
		}
	}
	
	private boolean noCorrectOptionPresent(List<Option> possibleAnswers) {
		return possibleAnswers.stream()
			.filter(Option::isCorrect)
			.count() == 0;
	}

	private boolean isNullOrBlank(String string) {
		return string == null || string.isBlank();
	}
	
}
