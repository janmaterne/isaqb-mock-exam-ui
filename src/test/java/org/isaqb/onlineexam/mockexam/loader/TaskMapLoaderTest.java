package org.isaqb.onlineexam.mockexam.loader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.isaqb.onlineexam.mockexam.model.I18NText;
import org.isaqb.onlineexam.mockexam.model.Language;
import org.isaqb.onlineexam.mockexam.model.Task;
import org.isaqb.onlineexam.mockexam.model.TaskType;
import org.isaqb.onlineexam.mockexam.model.TaskValidator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class TaskMapLoaderTest {

    @Nested
    public class PrintErrors {

    }



    @Nested
    public class Validate {

        TaskMapLoader loader = new TaskMapLoader(null, null, new TaskValidator(), null);

        @Test
        public void valid() {
            Task task = createTask();
            Map<String, List<String>> errorMap = new HashMap<>();
            Task validated = loader.validate(task, errorMap);
            assertEquals(task, validated);
            assertTrue(errorMap.isEmpty(), errorMap.toString());
        }

        private Task createTask() {
            return new Task("TestID", TaskType.CHOOSE, 1, new I18NText(Map.of(Language.DE, "test")));
        }

        @Test
        public void invalid() {
            Task task = createTask();
            task.setQuestion(new I18NText());
            Map<String, List<String>> errorMap = new HashMap<>();
            Task validated = loader.validate(task, errorMap);
            assertEquals(task, validated);
            assertTrue(errorMap.toString().contains("id=TestID"));
        }

        @Test
        public void invalidIdMissing() {
            Task task = createTask();
            task.setId(null);
            Map<String, List<String>> errorMap = new HashMap<>();
            Task validated = loader.validate(task, errorMap);
            assertEquals(task, validated);
            assertTrue(errorMap.toString().contains("hash="));
        }
    }



    @Nested
    public class LoadTasks {

        TaskMapLoader loader = new TaskMapLoader(null, null, null, null);

        @Test
        public void noErrors() {
            var msg = loader.errors2string(new HashMap<>());
            assertTrue(msg.isEmpty());
        }

        @Test
        public void error_1topic_1error() {
            var errors = Map.of("ddd", Arrays.asList("ddd-1"));
            var msg = ErrorQueue.of(loader, errors);
            msg.assertCountIs(3);
            msg.assertNextContains("validation errors");
            msg.assertNextContains("- topic ddd");
            msg.assertNextContains("  -- ddd-1");
        }

        @Test
        public void error_1topic_2errors() {
            var errors = Map.of("ddd", Arrays.asList("ddd-1", "ddd-2"));
            var msg = ErrorQueue.of(loader, errors);
            msg.assertCountIs(4);
            msg.assertNextContains("validation errors");
            msg.assertNextContains("- topic ddd");
            msg.assertNextContains("  -- ddd-1");
            msg.assertNextContains("  -- ddd-2");
        }

        @Test
        public void error_2topic_2errors() {
            var errors = Map.of(
                "ddd", Arrays.asList("ddd-1", "ddd-2"),
                "mock", Arrays.asList("mock-1", "mock-2")
            );
            var msg = ErrorQueue.of(loader, errors);
            msg.assertCountIs(7);
            msg.assertNextContains("validation errors");
            msg.assertNextContains("- topic ddd");
            msg.assertNextContains("  -- ddd-1");
            msg.assertNextContains("  -- ddd-2");
            msg.assertNextContains("- topic mock");
            msg.assertNextContains("  -- mock-1");
            msg.assertNextContains("  -- mock-2");
        }

    }



    public static class ErrorQueue {
        private Queue<String> errorQueue;
        public static ErrorQueue of(TaskMapLoader loader, Map<String, List<String>> errorMap) {
            String str = loader.errors2string(errorMap);
            return new ErrorQueue(
                new LinkedList<>(str.lines().toList())
            );
        }
        private ErrorQueue(Queue<String> errorQueue) {
            this.errorQueue = errorQueue;
        }
        public void assertNextContains(String substring) {
            String next = errorQueue.poll();
            assertTrue(next.contains(substring), next);
        }
        public void assertCountIs(int count) {
            assertEquals(count, errorQueue.size());
        }
    }

}
