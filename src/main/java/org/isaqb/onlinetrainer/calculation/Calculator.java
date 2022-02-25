package org.isaqb.onlinetrainer.calculation;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.isaqb.onlinetrainer.model.Exam;
import org.isaqb.onlinetrainer.model.Task;
import org.isaqb.onlinetrainer.model.TaskAnswer;
import org.isaqb.onlinetrainer.model.TaskType;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

/**
 * <p>There are three kinds of questions and all describe different ways
 * to count. But basically all are catched by this algorithm.</p>
 *
 * <p><b>A-Questions (Single Choice, Single Correct Answer):</b> Select the only correct
 * answer to a question from the list of possible answers. There is only one
 * correct answer. You receive the specified score for selecting the correct answer.</p>
 *
 * <p><b>P-Questions (Pick-from-many, Pick Multiple):</b> Select the number of correct
 * answers given in the text from the list of possible answers to a question. Select
 * just as many answers as are required in the introductory text. You receive 1/n of
 * the total points for each correct answer. For each incorrect cross, 1/n of the
 * points are deducted.</p>
 *
 * <p><b>K-Questions (Allocation Questions, Choose Category):</b> For a question, select
 * the correct of the two options for each answer choice ("correct" or "incorrect" or
 * "applicable" or "not applicable"). You will receive 1/n of the points for each
 * correctly placed cross. Incorrectly placed crosses result in the deduction of 1/n of
 * the points. If NO answer is selected in a line, there are neither points nor
 * deductions.</p>
 *
 * @see https://github.com/isaqb-org/examination-foundation/blob/master/raw/mock_exam/docs/preamble/introduction.adoc
 */
@Component
public class Calculator {

	public CalculationResult calculate(Exam exam, List<TaskAnswer> givenAnswers) {
		CalculationResult result = new CalculationResult();
		var answerMap = givenAnswers.stream().collect(Collectors.toMap(TaskAnswer::getTaskNumber, Function.identity()));
		AtomicInteger index = new AtomicInteger();
		result.points = exam.getTasks().stream()
			.map( t -> new CalcTaskData(t, answerMap.getOrDefault(index.incrementAndGet(), null)) )
			.collect( Collectors.toMap(CalcTaskData::id, CalcTaskData::calculate, this::first) );
		result.pointsMaximum = exam.possiblePoints();
		result.passed = result.totalPoints() >= exam.getRequiredPoints();
		return result;
	}
	
	private <T> T first(T first, T second) {
		System.out.printf("first()%n- %s%n- %s%n%n", first, second);
		return first;
	}



	public enum AnswerResult {
		CORRECT, INCORRECT, UNSELECTED
	}



	@AllArgsConstructor
	static class CalcTaskData {
		Task task;
		TaskAnswer answer;

		public String id() {
			return task.getId();
		}

		public double calculate() {
			if (answer == null) {
				return 0;
			}
			Map<AnswerResult, Integer> results =
				task.getType() == TaskType.CHOOSE
				? analyseChoose()
				: analyze();
			return calculate(
				task.getReachablePoints(),
				task.getPossibleOptions().size(),
				results.get(AnswerResult.CORRECT),
				results.get(AnswerResult.INCORRECT),
				results.get(AnswerResult.UNSELECTED)
			);
		}

		private Map<AnswerResult, Integer> analyseChoose() {
			if (answer == null) {
				return Map.of(
					AnswerResult.CORRECT, 0,
					AnswerResult.INCORRECT, 0,
					// no given answer means all unselected
					AnswerResult.UNSELECTED, task.getPossibleOptions().size()
				);
			}

			var map = Map.of(
				AnswerResult.CORRECT, new AtomicInteger(),
				AnswerResult.INCORRECT, new AtomicInteger(),
				AnswerResult.UNSELECTED, new AtomicInteger()
			);
			for(int i=0; i<task.getPossibleOptions().size(); i++) {
				var option = task.getPossibleOptions().get(i);
				var position = String.valueOf(option.getPosition());
				if (answer.getOptionSelections().containsKey(position)) {
					var given = answer.getOptionSelections().get(position);
					boolean correct = isCorrect(option.getCorrectColumnsIndices(), given);
					if (correct) {
						map.get(AnswerResult.CORRECT).incrementAndGet();
					} else {
						map.get(AnswerResult.INCORRECT).incrementAndGet();
					}
				} else {
					map.get(AnswerResult.UNSELECTED).incrementAndGet();
				}
			}

			// Use of Integer instead of AtomicInteger for safety reason
			return Map.of(
				AnswerResult.CORRECT, map.get(AnswerResult.CORRECT).get(),
				AnswerResult.INCORRECT, map.get(AnswerResult.INCORRECT).get(),
				AnswerResult.UNSELECTED, map.get(AnswerResult.UNSELECTED).get()
			);
		}

		private boolean isCorrect(List<Integer> correctColumnsIndices, List<String> given) {
			if (given == null || correctColumnsIndices.size() != given.size()) {
				return false;
			}
			for(int i=0; i<correctColumnsIndices.size(); i++) {
				int correctValue = correctColumnsIndices.get(i);
				int givenValue = parseInt(given.get(i));
				if (correctValue != givenValue) {
					return false;
				}
			}
			return true;
		}
		
		private int parseInt(String value) {
		    try {
		        return Integer.parseInt(value);
		    } catch (NumberFormatException e) {
		        return 0;
		    }
		}

		public Map<AnswerResult, Integer> analyze() {
			var map = Map.of(
				AnswerResult.CORRECT, new AtomicInteger(),
				AnswerResult.INCORRECT, new AtomicInteger(),
				AnswerResult.UNSELECTED, new AtomicInteger()
			);
			for(int i=0; i<task.getPossibleOptions().size(); i++) {
				var option = task.getPossibleOptions().get(i);
				var position = String.valueOf(option.getPosition());
				if (answer != null && answer.getOptionSelections().containsKey(position)) {
					var given = answer.getOptionSelections().get(position);
					if (selectionEquals(option.getColumnValues(), given)) {
						map.get(AnswerResult.CORRECT).incrementAndGet();
					} else {
						map.get(AnswerResult.INCORRECT).incrementAndGet();
					}
				} else {
					// no given answer could be right ...
					if (option.isCorrect()) {
						map.get(AnswerResult.UNSELECTED).incrementAndGet();
					} else {
						map.get(AnswerResult.CORRECT).incrementAndGet();
					}
				}
			}
			// Use of Integer instead of AtomicInteger for safety reason
			return Map.of(
				AnswerResult.CORRECT, map.get(AnswerResult.CORRECT).get(),
				AnswerResult.INCORRECT, map.get(AnswerResult.INCORRECT).get(),
				AnswerResult.UNSELECTED, map.get(AnswerResult.UNSELECTED).get()
			);
		}

		protected boolean selectionEquals(List<Character> expected, List<String> actual) {
			if (actual == null ||  expected.size() != actual.size()) {
				return false;
			}

			var exp = expected.stream()
				.map(Character::toLowerCase)
				.toList();
			var act = actual.stream()
				.map(String::toLowerCase)
				.map( s -> s.replace("on", "y") )
				.toList();

			for(int i=0; i<exp.size(); i++) {
				if (exp.get(i) != act.get(i).charAt(0)) {
					return false;
				}
			}
			return true;
		}

		// TODO Strategy per Type?
		private double calculate(int reachablePoints, int countTotal, int countCorrect, int countIncorrect, int countUnselected) {
			double pointsPerCorrect = (double)reachablePoints / countTotal;
			double points = pointsPerCorrect * (countCorrect - countIncorrect);
			return Math.max(0, roundCent(points));
		}

		private double roundCent(double value) {
			int i = (int) (100 * value);
			return 1.0 * i / 100;
		}
	}

}
