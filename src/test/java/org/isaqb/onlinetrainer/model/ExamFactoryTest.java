package org.isaqb.onlinetrainer.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.isaqb.onlinetrainer.DataConfiguration.ExamConfig;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class ExamFactoryTest {

    @Nested
    class MockExam {
        @Test
        void standard() {
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
        void standard() {
            var exam = standardFactoryBuilder().build()
                .examByQuestionIds("ddd-01,ddd-02");
            assertEquals(2, exam.getTasks().size());
            var ids = exam.getTasks().stream().map(Task::getId).toList();
            assertTrue(ids.contains("ddd-01"));
            assertTrue(ids.contains("ddd-02"));
        }

        @Test
        void idsFromMultipleTopics() {
            var exam = standardFactoryBuilder().build()
                .examByQuestionIds("mock-01,ddd-02,adoc-03");
            assertEquals(3, exam.getTasks().size());
            var ids = exam.getTasks().stream().map(Task::getId).toList();
            assertTrue(ids.contains("mock-01"));
            assertTrue(ids.contains("ddd-02"));
            assertTrue(ids.contains("adoc-03"));
        }

        @Test
        void notExistentId() {
            var exam = standardFactoryBuilder().build().examByQuestionIds("ddd-99");
            assertEquals(0, exam.getTasks().size());
        }
    }



    @Nested
    class ExamByTopic {

        @Test
        void standard() {
            var exam = standardFactoryBuilder().build().examByTopic("adoc");
            assertEquals(5, exam.getTasks().size());
            assertEquals(0,  exam.getTasks().stream()
                .filter( t -> !t.getId().startsWith("adoc") )
                .count()
            );
        }

        @Test
        void changedMaxSize() {
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
        void maxGreaterThanNumberOfQuestions() {
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
        void allTopics() {
            var exam = new FactoryBuilder()
                .task("mock", 1)
                .task("ddd", 1)
                .task("adoc", 1)
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
        void notExistentTopic() {
            var exam = standardFactoryBuilder().build().examByTopic("not-existent");
            assertEquals(0, exam.getTasks().size());
        }
    }



    public FactoryBuilder standardFactoryBuilder() {
        return new FactoryBuilder()
            .maxNumOfQuestions(5)
            .task("mock", 39)
            .task("ddd", 12)
            .task("adoc", 10)
            .task("cloudinfra", 7)
            .task("foundation-chapter1", 8)
            .task("foundation-chapter2", 7)
            .task("foundation-chapter3", 12)
            .task("foundation-chapter4", 6)
            .task("foundation", 33);
    }

    class FactoryBuilder {
        private int maxNumOfQuestions = 5;
        private Map<String, ExamConfig> exams = new HashMap<>();
        private Map<String, List<Task>> tasks = new HashMap<>();

        public ExamFactory build() {
            return ExamFactory.testInstance(maxNumOfQuestions, exams, tasks);
        }

        public FactoryBuilder maxNumOfQuestions(int max) {
            this.maxNumOfQuestions = max;
            return this;
        }

        public FactoryBuilder task(String topic, int count) {
            exam(topic);
            List<Task> list = tasks.computeIfAbsent(topic, (s) -> new ArrayList<Task>());
            IntStream.rangeClosed(1, count)
                .mapToObj( i -> "%s-%02d".formatted(topic, i) )
                .map(this::task)
                .forEach(list::add);
            return this;
        }

        private Task task(String id) {
            Task task = new Task();
            task.setId(id);
            return task;
        }

        public FactoryBuilder exam(String name) {
            ExamConfig cfg = exams.computeIfAbsent(name, (s) -> new ExamConfig());
            if (cfg.getTaskRefs() == null) {
                cfg.setTaskRefs(new ArrayList<>());
            }
            if (!cfg.getTaskRefs().contains(name)) {
                cfg.getTaskRefs().add(name);
            }
            return this;
        }
    }
}
