package org.isaqb.onlineexam.mockexam.model;

import static org.isaqb.onlineexam.mockexam.util.ArrayUtil.asList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.ToString;

@ToString
public class Exam {
	
	@Getter
	private double requiredPoints;
	@Getter
	private List<Task> tasks;
	private Map<Task, List<Character>> givenAnswers = new HashMap<>();
	
	public Exam(double requiredPoints, Task... tasks) {
		this(requiredPoints, List.of(tasks));
	}

	public Exam(double requiredPoints, List<Task> tasks) {
		this.requiredPoints = requiredPoints;
		this.tasks = tasks;
	}
	
	public Exam addGivenOption(Task task, char... answers) {
		givenAnswers.put(task, asList(answers));
		return this;
	}
	
	public int possiblePoints() {
		return tasks.stream().mapToInt(Task::getReachablePoints).sum();
	}

}
