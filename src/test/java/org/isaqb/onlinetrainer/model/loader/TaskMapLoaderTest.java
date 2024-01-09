package org.isaqb.onlinetrainer.model.loader;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.isaqb.onlinetrainer.DataConfiguration;
import org.isaqb.onlinetrainer.DataConfiguration.QuizConfig;
import org.isaqb.onlinetrainer.DataConfiguration.UrlTemplateConfig;
import org.isaqb.onlinetrainer.loader.TaskMapLoader;
import org.isaqb.onlinetrainer.loader.UrlLoader;
import org.isaqb.onlinetrainer.model.I18NText;
import org.isaqb.onlinetrainer.model.Language;
import org.isaqb.onlinetrainer.model.Task;
import org.isaqb.onlinetrainer.model.TaskMap;
import org.isaqb.onlinetrainer.model.TaskType;
import org.isaqb.onlinetrainer.model.TaskValidator;
import org.isaqb.onlinetrainer.taskparser.asciidoc.AsciidocTaskParser;
import org.isaqb.onlinetrainer.taskparser.simple.SimpleTaskParser;
import org.isaqb.onlinetrainer.taskparser.yaml.Yaml2ModelMapper;
import org.isaqb.onlinetrainer.taskparser.yaml.YamlTaskParser;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

public class TaskMapLoaderTest {

    @Nested
    class Errors2String {

        // Dummy class to make the method testable.
        class SystemUnderTest extends TaskMapLoader {
            public SystemUnderTest() {
                super(null, null, null, null, null, null);
            }
            public String test(Map<String, List<String>> errorMap) {
                return errors2string(errorMap);
            }
        }

        @Test
        void noErrors() {
            Map<String, List<String>> errorMap = Map.of();
            var error = new SystemUnderTest().test(errorMap);
            assertTrue(error.isBlank());
        }

        @Test
        void oneTopicOneError() {
            Map<String, List<String>> errorMap = Map.of(
                "topic", List.of("error1")
            );
            var error = new SystemUnderTest().test(errorMap);
            assertFalse(error.isBlank());
            assertTrue(error.contains("Tasks with validation errors:"));
            assertTrue(error.contains("topic"));
            assertTrue(error.contains("error1"));
        }

        @Test
        void oneTopicMultipleError() {
            Map<String, List<String>> errorMap = Map.of(
                "topic", List.of("error1", "error2")
            );
            var error = new SystemUnderTest().test(errorMap);
            assertFalse(error.isBlank());
            assertTrue(error.contains("Tasks with validation errors:"));
            assertTrue(error.contains("topic"));
            assertTrue(error.contains("error1"));
            assertTrue(error.contains("error2"));
        }

        @Test
        void multipleTopicsWithErrors() {
            Map<String, List<String>> errorMap = Map.of(
                "topic1", List.of("error1"),
                "topic2", List.of("error2")
            );
            var error = new SystemUnderTest().test(errorMap);
            assertFalse(error.isBlank());
            assertTrue(error.contains("Tasks with validation errors:"));
            assertTrue(error.contains("topic1"));
            assertTrue(error.contains("topic2"));
            assertTrue(error.contains("error1"));
            assertTrue(error.contains("error2"));
        }
    }


    @Nested
    class Validate {

        class SystemUnderTest extends TaskMapLoader {
            public SystemUnderTest() {
                super(null, new TaskValidator(), null, null, null, null);
            }
            public Task test(Task task, Map<String, List<String>> errorMap) {
                return validate(task, errorMap);
            }
        }

        @Test
        void noErrors() {
            Map<String, List<String>> errorMap = new HashMap<>();
            Task task = validTask();
            Task rv = new SystemUnderTest().test(task, errorMap);
            assertEquals(task, rv);
            assertTrue(errorMap.isEmpty());
        }

        @Test
        void withError() {
            Map<String, List<String>> errorMap = new HashMap<>();
            Task task = validTask();
            task.setReachablePoints(0);
            Task rv = new SystemUnderTest().test(task, errorMap);
            assertEquals(task, rv);
            assertFalse(errorMap.isEmpty());
            assertTrue(errorMap.keySet().iterator().next().startsWith("id="));
        }

        @Test
        void noId() {
            Map<String, List<String>> errorMap = new HashMap<>();
            Task task = validTask();
            task.setId(null);
            Task rv = new SystemUnderTest().test(task, errorMap);
            assertEquals(task, rv);
            assertFalse(errorMap.isEmpty());
            assertTrue(errorMap.keySet().iterator().next().startsWith("hash="));
        }

        private Task validTask() {
            I18NText question = new I18NText(Map.of(
                Language.DE, "Frage",
                Language.EN, "Question"
            ));
            return new Task("ID-X-1", TaskType.CHOOSE, 42, question);
        }
    }


    @Nested
    class LoadTasks {

        class SystemUnderTest extends TaskMapLoader {
            public SystemUnderTest(DataConfiguration config) {
                super(config, new TaskValidator(), new UrlLoader(), new AsciidocTaskParser(), new YamlTaskParser(Mappers.getMapper(Yaml2ModelMapper.class)), new SimpleTaskParser() );
            }
            public TaskMap test() {
                return loadTasks();
            }
        }

        // nicht hinterm Proxy
        @Test
        @Disabled
        void simple() {
            DataConfiguration config = new DataConfiguration();
            config.setTasks(Map.of(
                "testTopic", new QuizConfig().setUrls(List.of(
                        new UrlTemplateConfig().setFrom(1).setTo(3).setUrlTemplate("https://raw.githubusercontent.com/isaqb-org/examination-foundation/main/raw/mock_exam/docs/questions/question-{NR}.adoc")
                    ))
            ));
            var taskMap = new SystemUnderTest(config).loadTasks();
            assertEquals(3, taskMap.getTasks().get("testTopic").size());
        }
    }

}
