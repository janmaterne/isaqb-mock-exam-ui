package org.isaqb.onlinetrainer.loader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.isaqb.onlinetrainer.DataConfiguration;
import org.isaqb.onlinetrainer.DataConfiguration.QuizConfig;
import org.isaqb.onlinetrainer.DataConfiguration.UrlTemplateConfig;
import org.isaqb.onlinetrainer.model.I18NText;
import org.isaqb.onlinetrainer.model.Language;
import org.isaqb.onlinetrainer.model.Task;
import org.isaqb.onlinetrainer.model.TaskType;
import org.isaqb.onlinetrainer.model.TaskValidator;
import org.isaqb.onlinetrainer.taskparser.asciidoc.AsciidocTaskParser;
import org.isaqb.onlinetrainer.taskparser.yaml.Yaml2ModelMapper;
import org.isaqb.onlinetrainer.taskparser.yaml.YamlTaskParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import lombok.SneakyThrows;

public class TaskMapLoaderTest {

    @Nested
    public class PrintErrors {

    }


    @Nested
    class Validate {

        TaskMapLoader loader = new TaskMapLoader( null, new TaskValidator(), null, null, null);

        @Test
        void valid() {
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
        void invalid() {
            Task task = createTask();
            task.setQuestion(new I18NText());
            Map<String, List<String>> errorMap = new HashMap<>();
            Task validated = loader.validate(task, errorMap);
            assertEquals(task, validated);
            assertTrue(errorMap.toString().contains("id=TestID"));
        }

        @Test
        void invalidIdMissing() {
            Task task = createTask();
            task.setId(null);
            Map<String, List<String>> errorMap = new HashMap<>();
            Task validated = loader.validate(task, errorMap);
            assertEquals(task, validated);
            assertTrue(errorMap.toString().contains("hash="));
        }
    }


    @Nested
    class LoadTasks {

        TaskMapLoader loader;

        @BeforeEach
        void init() {
            loader = new TaskMapLoader(null, null, null, null, null);
        }

        @Test
        void noErrors() {
            var msg = loader.errors2string(new HashMap<>());
            assertTrue(msg.isEmpty());
        }

        @Test
        void error_1topic_1error() {
            var errors = Map.of("ddd", Arrays.asList("ddd-1"));
            var msg = ErrorQueue.of(loader, errors);
            msg.assertCountIs(3);
            msg.assertNextContains("validation errors");
            msg.assertNextContains("- topic ddd");
            msg.assertNextContains("  -- ddd-1");
        }

        @Test
        void error_1topic_2errors() {
            var errors = Map.of("ddd", Arrays.asList("ddd-1", "ddd-2"));
            var msg = ErrorQueue.of(loader, errors);
            msg.assertCountIs(4);
            msg.assertNextContains("validation errors");
            msg.assertNextContains("- topic ddd");
            msg.assertNextContains("  -- ddd-1");
            msg.assertNextContains("  -- ddd-2");
        }

        @Test
        void error_2topic_2errors() {
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

        @Test
        @SneakyThrows
        void loadExistingTasks() {
            String topic = "test";
            loader = new TaskMapLoader(
                new ConfigBuilder()
                    .withTaskFile(topic, new File("src/test/resources/ParserTest/question-01.adoc"))
                    .withTaskFile(topic, new File("src/test/resources/ParserTest/question-04.yaml"))
                    .build(),
                new TaskValidator(),
                new UrlLoader(),
                new AsciidocTaskParser(),
                new YamlTaskParser(Mappers.getMapper(Yaml2ModelMapper.class))
            );

            var tasks = loader.loadTasks();
            var x = tasks.getTasks().get(topic);

            System.out.println(x.get(0));
            System.out.println();
            System.out.println(x.get(1));

            assertEquals(2, tasks.getTasks().get(topic).size());
        }
    }


    class ConfigBuilder {

        private DataConfiguration config;

        public ConfigBuilder() {
            config = new DataConfiguration();
            config.setTasks(new HashMap<>());
        }

        public ConfigBuilder withTaskFile(String topic, File resource) {
            if (!config.getTasks().containsKey(topic)) {
                config.getTasks().put(topic, new QuizConfig());
            }
            config.getTasks().get(topic).getUrls().add(urlTemplate(resource));
            return this;
        }

        @SneakyThrows
        private UrlTemplateConfig urlTemplate(File resource) {
            UrlTemplateConfig cfg = new UrlTemplateConfig();
            cfg.setFrom(1);
            cfg.setTo(1);
            cfg.setUrlTemplate(resource.toURI().toURL().toString());
            return cfg;
        }

        public DataConfiguration build() {
            return config;
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
