package org.isaqb.onlinetrainer.calculation;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.isaqb.onlinetrainer.calculation.Calculator.AnswerResult;
import org.isaqb.onlinetrainer.model.Exam;
import org.isaqb.onlinetrainer.model.I18NText;
import org.isaqb.onlinetrainer.model.Option;
import org.isaqb.onlinetrainer.model.Task;
import org.isaqb.onlinetrainer.model.TaskAnswer;
import org.isaqb.onlinetrainer.model.TaskType;
import org.isaqb.onlinetrainer.testutil.TaskLoader4Test;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class CalculationTest {

    @Nested
    class CalcDataSelectionEquals {

        @Test
        void equals() {
			assertTrue(new Calculator.CalcTaskData(null, null).selectionEquals(
				asList(Character.valueOf('a'), Character.valueOf('b')),
				asList("a", "b")
			));
		}

        @Test
        void alsoEquals() {
			assertTrue(new Calculator.CalcTaskData(null, null).selectionEquals(
				asList(Character.valueOf('a'), Character.valueOf('b')),
				asList("apple", "bear")
			));
		}

        @Test
        void actualIsNull() {
			assertFalse(new Calculator.CalcTaskData(null, null).selectionEquals(
				asList(Character.valueOf('a'), Character.valueOf('b')),
				null
			));
		}

        @Test
        void differ() {
			assertFalse(new Calculator.CalcTaskData(null, null).selectionEquals(
				asList(Character.valueOf('a'), Character.valueOf('b')),
				asList("apple", "else")
			));
		}
	}


    @Nested
    class CalcDataAnalyze {

        @Test
        void noGivenAnswer() {
			var data = new Calculator.CalcTaskData(
					new Task()
						.addPossibleOption(new Option('a', true, new I18NText()))
						.addPossibleOption(new Option('b', false, new I18NText())),
					null
				);
			var map = data.analyze();
			assertEquals(1, map.get(AnswerResult.CORRECT));
			// analyzed to 'unselected' but evaluated to 'incorrect' during calculation
			assertEquals(0, map.get(AnswerResult.INCORRECT));
			assertEquals(1, map.get(AnswerResult.UNSELECTED));
		}

        @Test
        void checkboxOk() {
			var data = new Calculator.CalcTaskData(
					new Task()
						.addPossibleOption(new Option('a', true, new I18NText()))
						.addPossibleOption(new Option('b', false, new I18NText())),
					new TaskAnswer().put("a", "y")
				);
			var map = data.analyze();
			assertEquals(2, map.get(AnswerResult.CORRECT));
			assertEquals(0, map.get(AnswerResult.INCORRECT));
			assertEquals(0, map.get(AnswerResult.UNSELECTED));
		}

        @Test
        void checkboxWrong() {
			var data = new Calculator.CalcTaskData(
					new Task()
						.addPossibleOption(new Option('a', true, new I18NText()))
						.addPossibleOption(new Option('b', false, new I18NText())),
					new TaskAnswer().put("b", "y")
				);
			var map = data.analyze();
			assertEquals(0, map.get(AnswerResult.CORRECT));
			assertEquals(1, map.get(AnswerResult.INCORRECT));
			assertEquals(1, map.get(AnswerResult.UNSELECTED));
		}

        @Test
        void checkboxBothOk() {
			var data = new Calculator.CalcTaskData(
					new Task()
						.addPossibleOption(new Option('a', true, new I18NText()))
						.addPossibleOption(new Option('b', true, new I18NText())),
					new TaskAnswer().put("a", "y").put("b", "y")
				);
			var map = data.analyze();
			assertEquals(2, map.get(AnswerResult.CORRECT));
			assertEquals(0, map.get(AnswerResult.INCORRECT));
			assertEquals(0, map.get(AnswerResult.UNSELECTED));
		}

        @Test
        void checkboxBothWrong() {
			var data = new Calculator.CalcTaskData(
					new Task()
						.addPossibleOption(new Option('a', false, new I18NText()))
						.addPossibleOption(new Option('b', false, new I18NText())),
					new TaskAnswer().put("a", "y").put("b", "y")
				);
			var map = data.analyze();
			assertEquals(0, map.get(AnswerResult.CORRECT));
			assertEquals(2, map.get(AnswerResult.INCORRECT));
			assertEquals(0, map.get(AnswerResult.UNSELECTED));
		}
	}


    @Nested
    class CalcDataCalculate {

        @Test
        void fourOfFour() {
			var data = new Calculator.CalcTaskData(
				new Task(null, TaskType.PICK_FROM_MANY, 10, null)
					.addPossibleOption(new Option('a', true, new I18NText()))
					.addPossibleOption(new Option('b', true, new I18NText()))
					.addPossibleOption(new Option('c', true, new I18NText()))
					.addPossibleOption(new Option('d', true, new I18NText())),
				new TaskAnswer().put("a", "y").put("b", "y").put("c", "y").put("d", "y")
			);
			assertEquals(10, data.calculate());
		}

        @Test
        void threeOfFour() {
			var data = new Calculator.CalcTaskData(
				new Task(null, TaskType.PICK_FROM_MANY, 10, null)
					.addPossibleOption(new Option('a', true, new I18NText()))
					.addPossibleOption(new Option('b', true, new I18NText()))
					.addPossibleOption(new Option('c', true, new I18NText()))
					.addPossibleOption(new Option('d', true, new I18NText())),
				new TaskAnswer().put("a", "y").put("b", "y").put("c", "y").put("d", "n")
			);
			assertEquals(5, data.calculate());
		}
	}



	@Nested
	public class CalculateRealworldData {

		TaskLoader4Test taskLoader = TaskLoader4Test.create("ParserTest");
		Calculator calculator = new Calculator();

        @Test
        void question01_3of3() throws IOException {
			Task task = taskLoader.loadTask("question-01.adoc");
			Exam exam = Exam.createExam(42, task);
			TaskAnswer answer = new TaskAnswer(1)
				.put("a", "n")
				.put("b", "n")
				.put("c", "y");
			var result = calculator.calculate(exam, asList(answer));
			var points = result.points.get(task.getId());
			assertEquals(1, points);
		}

        @Test
        void question01_3of3_asViaUI() throws IOException {
			Task task = taskLoader.loadTask("question-01.adoc");
			Exam exam = Exam.createExam(42, task);
			TaskAnswer answer = new TaskAnswer(1)
				// not selected checkboxes are not transfered, so no "n"-values
				.put("c", "on");
			var result = calculator.calculate(exam, asList(answer));
			var points = result.points.get(task.getId());
			assertEquals(1, points);
		}

        @Test
        void question01_0of3() throws IOException {
			Task task = taskLoader.loadTask("question-01.adoc");
			Exam exam = Exam.createExam(42, task);
			TaskAnswer answer = new TaskAnswer(1)
				.put("a", "y")
				.put("b", "y")
				.put("c", "n");
			var result = calculator.calculate(exam, asList(answer));
			var points = result.points.get(task.getId());
			assertEquals(0, points);
		}

        @Test
        void question01_2of3() throws IOException {
			Task task = taskLoader.loadTask("question-01.adoc");
			Exam exam = Exam.createExam(42, task);
			TaskAnswer answer = new TaskAnswer(1)
				.put("a", "y")
				.put("b", "n")
				.put("c", "y");
			var result = calculator.calculate(exam, asList(answer));
			var points = result.points.get(task.getId());
			assertEquals(0.33, points);
		}

//		@Test
		public void question04() throws IOException {
			Task task = taskLoader.loadTask("question-04.adoc");
			Exam exam = Exam.createExam(42, task);
			TaskAnswer answer = new TaskAnswer(1)
				.put("a", asList("y", "n"))
				.put("b", asList("y", "n"))
				.put("c", asList("n", "y"));
			var result = calculator.calculate(exam, asList(answer));
			var points = result.points.get(task.getId());
			assertEquals(2.0, points);
		}
	}

}
