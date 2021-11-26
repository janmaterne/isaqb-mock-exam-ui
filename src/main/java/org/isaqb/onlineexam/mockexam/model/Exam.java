package org.isaqb.onlineexam.mockexam.model;

import static org.isaqb.onlineexam.mockexam.util.ArrayUtil.asList;

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
	@Accessors(chain = true)
	private Mode mode;



	public static Exam createExam(double requiredPoints, Task... tasks) {
	    return createExam(requiredPoints, List.of(tasks));
	}

    public static Exam createExam(double requiredPoints, List<Task> tasks) {
        return new Exam(requiredPoints, tasks, new HashMap<>(), Mode.EXAM);
    }

    public static Exam createQuiz(List<Task> tasks) {
        return new Exam(0, tasks, new HashMap<>(), Mode.QUIZ);
    }



	public Exam addGivenOption(Task task, char... answers) {
		givenAnswers.put(task, asList(answers));
		return this;
	}

	public int possiblePoints() {
		return tasks.stream().mapToInt(Task::getReachablePoints).sum();
	}

}
