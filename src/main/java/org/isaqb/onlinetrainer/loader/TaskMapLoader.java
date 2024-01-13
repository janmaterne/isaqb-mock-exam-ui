package org.isaqb.onlinetrainer.loader;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;

import org.isaqb.onlinetrainer.DataConfiguration;
import org.isaqb.onlinetrainer.model.Task;
import org.isaqb.onlinetrainer.model.TaskMap;
import org.isaqb.onlinetrainer.model.TaskValidator;
import org.isaqb.onlinetrainer.taskparser.TaskParser;
import org.isaqb.onlinetrainer.taskparser.TaskParserChain;
import org.isaqb.onlinetrainer.taskparser.asciidoc.AsciidocTaskParser;
import org.isaqb.onlinetrainer.taskparser.simple.SimpleTaskParser;
import org.isaqb.onlinetrainer.taskparser.yaml.YamlTaskParser;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Component
@Slf4j
public class TaskMapLoader {

    private final DataConfiguration config;
    private final TaskValidator validator;
    private final Loader loader;

    private final AsciidocTaskParser adocParser;
    private final YamlTaskParser yamlParser;
    private final SimpleTaskParser simpleParser;

    @Bean
    public TaskMap loadTasks() {
        var taskMap = new TaskMap();
        var errorMap = new HashMap<String,List<String>>();
        for(var entry : config.getTasks().entrySet()) {
            String topic = entry.getKey();
            var tasks = entry.getValue().generateUrls()
                .sorted()
                .distinct()
                .map(this::url2task)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map( t -> validate(t, errorMap) )
                .toList();
            if (!tasks.isEmpty()) {
                taskMap.getTasks().put(topic, tasks);
                log.info("Task topic '{}' configured with {} tasks.", topic, tasks.size());
            } else {
                log.error("Task topic '{}' configured without any tasks.", topic);
            }
        }
        printErrors(errorMap);
        return taskMap;
    }

	private Optional<Task> url2task(String url) {
        TaskParser parser = guessParser(url);
        log.debug("parse {} with {}", url, parser.getClass().getSimpleName());
        return loader
            .loadAsString(url)
            .map(parser::parseContent);
    }

    private TaskParser guessParser(String url) {
        String suffix = url.substring(url.lastIndexOf('.')+1);
        return switch (suffix) {
            case "adoc" -> adocParser;
            case "yaml" -> yamlParser;
            case "txt" -> simpleParser;
            default -> new TaskParserChain(yamlParser, simpleParser, adocParser);
        };
    }

    protected Task validate(Task task, Map<String, List<String>> errorMap) {
        var errors = validator.validate(task);
        var key = task.getId() != null
                ? "id=" + task.getId()
                : "hash=" + task.hashCode();
        if (!errors.isEmpty()) {
            errorMap.put(key, errors);
        }
        return task;
    }

    private void printErrors(Map<String, List<String>> errorMap) {
        String errorMessage = errors2string(errorMap);
        if (!errorMessage.isBlank()) {
            log.error(errorMessage);
        } else {
            log.debug("All tasks are valid.");
        }
    }

    protected String errors2string(Map<String, List<String>> errorMap) {
        StringBuilder sb = new StringBuilder();
        long numOfErrors = errorMap.values().stream()
            .map(Collection::stream)
            .count();
        if (numOfErrors > 0) {
            sb.append("Tasks with validation errors:\n");
            var topics = new TreeSet<>(errorMap.keySet());
            for(var topic : topics) {
                var list = errorMap.get(topic);
                if (!list.isEmpty()) {
                    sb.append("- topic ").append(topic).append("\n");
                    list.forEach(err -> sb.append("  -- ").append(err).append("\n") );
                }
            }
        }
        return sb.toString();
    }

}
