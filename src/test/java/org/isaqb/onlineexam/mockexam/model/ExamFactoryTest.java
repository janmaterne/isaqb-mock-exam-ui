package org.isaqb.onlineexam.mockexam.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ExamFactoryTest {

    @Nested
    class MockExam {
        @Test
        public void standard() {
            var exam = standardFactoryBuilder().build().mockExam();
            assertEquals(39, exam.getTasks().size());
            assertEquals(0,  exam.getTasks().stream()
                .filter( t -> !t.getId().startsWith("mock") )
                .count()
            );
        }
    }



    @Nested
    class ExamByIds {
        @Test
        public void standard() {
            var exam = standardFactoryBuilder().build()
                .examByQuestionIds("ddd-01,ddd-02");
            assertEquals(2, exam.getTasks().size());
            var ids = exam.getTasks().stream().map(Task::getId).toList();
            assertTrue(ids.contains("ddd-01"));
            assertTrue(ids.contains("ddd-02"));
        }

        @Test
        public void idsFromMultipleTopics() {
            var exam = standardFactoryBuilder().build()
                .examByQuestionIds("mock-01,ddd-02,adoc-03");
            assertEquals(3, exam.getTasks().size());
            var ids = exam.getTasks().stream().map(Task::getId).toList();
            assertTrue(ids.contains("mock-01"));
            assertTrue(ids.contains("ddd-02"));
            assertTrue(ids.contains("adoc-03"));
        }

        @Test
        public void notExistentId() {
            var exam = standardFactoryBuilder().build().examByQuestionIds("ddd-99");
            assertEquals(0, exam.getTasks().size());
        }
    }



    @Nested
    class ExamByTopic {

        @Test
        public void standard() {
            var exam = standardFactoryBuilder().build().examByTopic("adoc");
            assertEquals(5, exam.getTasks().size());
            assertEquals(0,  exam.getTasks().stream()
                .filter( t -> !t.getId().startsWith("adoc") )
                .count()
            );
        }

        @Test
        public void changedMaxSize() {
            var exam = standardFactoryBuilder()
                .maxNumOfQuestions(3)
                .build()
                .examByTopic("adoc");
            assertEquals(3, exam.getTasks().size());
            assertEquals(0,  exam.getTasks().stream()
                .filter( t -> !t.getId().startsWith("adoc") )
                .count()
            );
        }

        @Test
        public void maxGreaterThanNumberOfQuestions() {
            var exam = standardFactoryBuilder()
                .maxNumOfQuestions(100)
                .build()
                .examByTopic("adoc");
            assertEquals(10, exam.getTasks().size());
            assertEquals(0,  exam.getTasks().stream()
                .filter( t -> !t.getId().startsWith("adoc") )
                .count()
            );
        }

        @Test
        public void allTopics() {
            var exam = new FactoryBuilder()
                .mockTasks(1)
                .quizTask("ddd", 1)
                .quizTask("adoc", 1)
                .maxNumOfQuestions(5)
                .build()
                .examByTopic("-ALL");
            assertEquals(3, exam.getTasks().size());
            var ids = exam.getTasks().stream().map(Task::getId).toList();
            assertTrue(ids.contains("mock-01"));
            assertTrue(ids.contains("ddd-01"));
            assertTrue(ids.contains("adoc-01"));
        }

        @Test
        public void notExistentTopic() {
            var exam = standardFactoryBuilder().build().examByTopic("not-existent");
            assertEquals(0, exam.getTasks().size());
        }
    }



    public FactoryBuilder standardFactoryBuilder() {
        return new FactoryBuilder()
            .maxNumOfQuestions(5)
            .mockTasks(39)
            .quizTask("ddd", 12)
            .quizTask("adoc", 10)
            .quizTask("cloudinfra", 7)
            .quizTask("foundation-chapter1", 8)
            .quizTask("foundation-chapter2", 7)
            .quizTask("foundation-chapter3", 12)
            .quizTask("foundation-chapter4", 6)
            .quizTask("foundation", 33);
    }

    class FactoryBuilder {
        private int maxNumOfQuestions = 5;
        private List<Task> mockTasks = new ArrayList<>();
        private Map<String, List<Task>> tasks = new HashMap<>();

        public ExamFactory build() {
            return new ExamFactory(maxNumOfQuestions, new Exam(42, mockTasks), tasks);
        }

        public FactoryBuilder maxNumOfQuestions(int max) {
            this.maxNumOfQuestions = max;
            return this;
        }

        public FactoryBuilder mockTask(String id) {
            Task task = new Task();
            task.setId(id);
            mockTasks.add(task);
            return this;
        }

        public FactoryBuilder mockTasks(int count) {
            IntStream.rangeClosed(1, count)
                .mapToObj( i -> String.format("mock-%02d", i) )
                .forEach(this::mockTask);
            return this;
        }

        public FactoryBuilder quizTask(String topic, String taskId) {
            Task task = new Task();
            task.setId(taskId);
            tasks.computeIfAbsent(topic, (s) -> new ArrayList<>() ).add(task);
            return this;
        }

        public FactoryBuilder quizTask(String topic, int count) {
            IntStream.rangeClosed(1, count)
                .mapToObj( i -> String.format("%s-%02d", topic, i) )
                .forEach( id -> quizTask(topic, id) );
            return this;
        }
    }
}
