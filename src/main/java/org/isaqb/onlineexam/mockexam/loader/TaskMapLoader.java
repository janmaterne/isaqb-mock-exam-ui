package org.isaqb.onlineexam.mockexam.loader;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.isaqb.onlineexam.mockexam.DataConfiguration;
import org.isaqb.onlineexam.mockexam.DataConfiguration.UrlTemplateConfig;
import org.isaqb.onlineexam.mockexam.model.Task;
import org.isaqb.onlineexam.mockexam.model.TaskMap;
import org.isaqb.onlineexam.mockexam.model.TaskValidator;
import org.isaqb.onlineexam.mockexam.parser.TaskParser;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
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

    private Task validate(Task task, Map<String, List<String>> errorMap) {
        var errors = validator.validate(task);
        var key = task.getId() != null 
                ? "id=" + task.getId() 
                : "hash" + task.hashCode();
        if (!errors.isEmpty()) {
            errorMap.put(key, errors);
        }
        return task;
    }

    private void printErrors(HashMap<String, List<String>> errorMap) {
        long numOfErrors = errorMap.values().stream()
            .map(Collection::stream)
            .count();
        if (numOfErrors > 0) {
            System.err.println("Tasks with validation errors:");
            for(var e : errorMap.entrySet()) {
                System.err.printf("- topic '%s'%n", e.getKey());
                e.getValue().forEach(err -> System.out.printf("  -- %s%n", err) );
            }
        } else {
            System.err.println("All tasks are valid.");
        }
    }

}
