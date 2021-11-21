package org.isaqb.onlineexam.mockexam.loader;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;

import org.isaqb.onlineexam.mockexam.DataConfiguration;
import org.isaqb.onlineexam.mockexam.DataConfiguration.UrlTemplateConfig;
import org.isaqb.onlineexam.mockexam.model.Task;
import org.isaqb.onlineexam.mockexam.model.TaskMap;
import org.isaqb.onlineexam.mockexam.model.TaskValidator;
import org.isaqb.onlineexam.mockexam.parser.TaskParser;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Component
@Slf4j
public class TaskMapLoader {

    private DataConfiguration config;
    private TaskParser taskParser;
    private TaskValidator validator;
    private Loader loader;

    @Bean
    public TaskMap loadTasks() {
        var taskMap = new TaskMap();
        var errorMap = new HashMap<String,List<String>>();
        for(var entry : config.getTasks().entrySet()) {
            String topic = entry.getKey();
            var tasks = entry.getValue().getUrls().stream()
                .map(UrlTemplateConfig::generateUrls)
                .flatMap(Collection::stream)
                .distinct()
                .map(loader::loadAsString)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(taskParser::parseADoc)
                .map( t -> validate(t, errorMap) )
                .toList();
            if (!tasks.isEmpty()) {
                taskMap.getTasks().put(topic, tasks);
            } else {
                System.err.printf("Task topic '%s' configured without any tasks.%n", topic);
            }
        }
        printErrors(errorMap);
        return taskMap;
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
        if (errorMessage.isBlank()) {
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
            var keys = new TreeSet<>(errorMap.keySet());
            for(var key : keys) {
                var list = errorMap.get(key);
                if (!list.isEmpty()) {
                    sb.append("- topic ").append(key).append("\n");
                    list.forEach(err -> sb.append("  -- ").append(err).append("\n") );
                }
            }
        }
        return sb.toString();
    }

}
