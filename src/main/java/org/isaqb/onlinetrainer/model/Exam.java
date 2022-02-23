package org.isaqb.onlinetrainer.model;

import static org.isaqb.onlinetrainer.util.ArrayUtil.asList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@AllArgsConstructor
@ToString
@Accessors(chain = true)
public class Exam {

    public static enum Mode {
        QUIZ,
        EXAM;
        public static Mode of(String mode) {
            return "exam".equalsIgnoreCase(mode) ? EXAM : QUIZ;
        }
    }


	@Getter
	private double requiredPoints;
	@Getter
	private List<Task> tasks;
	private Map<Task, List<Character>> givenAnswers;
	@Getter
	@Setter
	private Mode mode;
	@Getter
	@Setter
	private String name;



	public static Exam createExam(double requiredPoints, Task... tasks) {
	    return createExam(requiredPoints, List.of(tasks));
	}

    public static Exam createExam(double requiredPoints, List<Task> tasks) {
        return new Exam(requiredPoints, tasks, new HashMap<>(), Mode.EXAM, null);
    }

    public static Exam createQuiz(List<Task> tasks) {
        return new Exam(0, tasks, new HashMap<>(), Mode.QUIZ, null);
    }



	public Exam addGivenOption(Task task, char... answers) {
		givenAnswers.put(task, asList(answers));
		return this;
	}

	public int possiblePoints() {
		return tasks.stream().mapToInt(Task::getReachablePoints).sum();
	}

}
