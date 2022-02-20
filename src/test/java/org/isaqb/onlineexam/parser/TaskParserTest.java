package org.isaqb.onlineexam.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.isaqb.onlineexam.model.Language;
import org.isaqb.onlineexam.model.Option;
import org.isaqb.onlineexam.model.Task;
import org.isaqb.onlineexam.model.TaskType;
import org.isaqb.onlineexam.testutil.TaskLoader4Test;
import org.junit.jupiter.api.Test;

public class TaskParserTest {

    private static TaskLoader4Test taskLoader = TaskLoader4Test.create("ParserTest");

    @Test
    public void realLifeQuestion01_ATask() throws IOException {
        // Original document copied from
        // https://raw.githubusercontent.com/isaqb-org/examination-foundation/master/raw/mock_exam/docs/questions/question-01.adoc
        Task task = taskLoader.loadTask("question-01.adoc");

        // id, points
        assertEquals("Q-20-04-01", task.getId());
        assertEquals(1, task.getReachablePoints());
        assertEquals(TaskType.SINGLE_CHOICE, task.getType());

        // question
        assertEquals("Wie viele Definitionen des Begriffes \"Softwarearchitektur\" gibt es?", task.getQuestion().getText(Language.DE));
        assertEquals("How many definitions of “software architecture” exist?", task.getQuestion().getText(Language.EN));

        // answer
        assertPositionsPresentUpTo(task, 'c');
        assertFalse(findOptionInTask(task, 'a').isCorrect());
        assertFalse(findOptionInTask(task, 'b').isCorrect());
        assertTrue(findOptionInTask(task, 'c').isCorrect());
        assertEquals(
            "Genau eine für alle Arten von Systemen.",
            findOptionInTask(task, 'a').getText(Language.DE)
        );
        assertEquals(
            "Eine für jede Art von Softwaresystem (z.{nbsp}B. \"eingebettet\", \"Echtzeit\", \"Entscheidungsunterstützung\", \"Web\", \"Batch\", …)",
            findOptionInTask(task, 'b').getText(Language.DE)
        );
        assertEquals(
            "Ein Dutzend oder mehr unterschiedliche Definitionen.",
            findOptionInTask(task, 'c').getText(Language.DE)
        );
        assertEquals(
            "Exactly one for all kinds of systems.",
            findOptionInTask(task, 'a').getText(Language.EN)
        );
        assertEquals(
            "One for every kind of software system (e.g. “embedded”, “real-time”, “decision support”, “web”, “batch”, ...).",
            findOptionInTask(task, 'b').getText(Language.EN)
        );
        assertEquals(
            "A dozen or more different definitions.",
            findOptionInTask(task, 'c').getText(Language.EN)
        );
    }

    @Test
    public void realLifeQuestion02_PTask() throws IOException {
        Task task = taskLoader.loadTask("question-02.adoc");

        assertEquals("Q-20-04-02", task.getId());
        assertEquals(1, task.getReachablePoints());
        assertEquals(TaskType.PICK_FROM_MANY, task.getType());

        assertNotNull(task.getQuestion());
        assertFalse(task.getPossibleOptions().isEmpty());

        assertPositionsPresentUpTo(task, 'e');

        assertEquals(
            "(interne und externe) Schnittstellen",
            task.findOptionByPosition('c').get().getText(Language.DE)
        );
    }

    @Test
    public void realLifeQuestion03() throws IOException {
        Task task = taskLoader.loadTask("question-03.adoc");
        assertPositionsPresentUpTo(task, 'g');
    }

    @Test
    public void realLifeQuestion04_KTask() throws IOException {
        Task task = taskLoader.loadTask("question-04.adoc");

        assertEquals("Q-17-13-02", task.getId());
        assertEquals(2, task.getReachablePoints());
        assertEquals(TaskType.CHOOSE, task.getType());

        assertNotNull(task.getQuestion());
        assertFalse(task.getPossibleOptions().isEmpty());
        assertEquals(3,  task.getPossibleOptions().size());

        assertEquals(0, findOptionInTask(task, 'a').getFirstCorrectColumnIndex().get());
        assertEquals(0, findOptionInTask(task, 'b').getFirstCorrectColumnIndex().get());
        assertEquals(1, findOptionInTask(task, 'c').getFirstCorrectColumnIndex().get());

        assertFalse(task.getColumnHeaders().isEmpty(), ()->"No headers");
        assertEquals(2, task.getColumnHeaders().size());
        assertHeaderContains(task, "Geeignet");
        assertHeaderContains(task, "Nicht geeignet");
        assertHeaderContains(task, "appropriate");
        assertHeaderContains(task, "not appropriate");

        assertPositionsPresentUpTo(task, 'c');
    }

    @Test
    public void multiLineQuestion() throws IOException {
        Task task = taskLoader.loadTask("question-test.adoc");
        String question = task.getQuestion().getText(Language.DE);
        assertTrue(question.contains("Question-Zeile 1"), "Zeile 1");
        assertTrue(question.contains("Question-Zeile 2"), "Zeile 2");
        assertTrue(question.contains("Question-Zeile 4"), "Zeile 4");
    }

    @Test
    public void multilineOption() throws IOException {
        Task task = taskLoader.loadTask("question-multilineOption.adoc");
        assertEquals("Question-Zeile 1", task.getQuestion().getText(Language.DE));
        String option = task.getPossibleOptions().get(0).getText(Language.DE);
        assertTrue(option.contains("Option Zeile 1"), "Zeile 1");
        assertTrue(option.contains("Option Zeile 2"), "Zeile 2");
        assertTrue(option.contains("Option Zeile 4"), "Zeile 4");
    }

    @Test
    public void explanation() throws IOException {
        Task task = taskLoader.loadTask("question-explanation.adoc");
        assertNotNull(task.getExplanation());
        assertTrue(task.getExplanation().startsWith("This is an *adoc* explanation."));
        assertTrue(task.getExplanation().contains("useless"));
        assertTrue(task.getExplanation().trim().endsWith("good informations."));
    }

    @Test
    public void optionWithAsciidoc() throws IOException {
        Task task = taskLoader.loadTask("question-28.adoc");
        Option option = task.getPossibleOptions().stream().filter( o -> o.getPosition()=='d' ).findFirst().get();
        assertTrue(option.getText().getText(Language.DE).contains("{nbsp}"));
        assertFalse(option.getText().getText(Language.DE).contains("|"));
    }



    private void assertHeaderContains(Task task, String expected) {
        assertTrue(task.getColumnHeaders().stream()
                .flatMap( i18n -> i18n.getMap().values().stream() )
                .filter( text -> text.equals(expected) )
                .findFirst()
                .isPresent(),
            () -> "Expected header: " + expected
        );
    }

    private void assertPositionsPresentUpTo(Task task, char lastPosition) {
        List<Character> expected = new ArrayList<>();
        for(int i = 'a'; i<=lastPosition; i++) {
            expected.add( (char)i );
        }

        var actual = task.getPossibleOptions().stream().map(Option::getPosition).toList();
        assertEquals(expected, actual, "Option-Positions missing");
    }

    private Option findOptionInTask(Task task, char position) {
        return Option.findByPosition(task.getPossibleOptions(), position).get(0);
    }

}
