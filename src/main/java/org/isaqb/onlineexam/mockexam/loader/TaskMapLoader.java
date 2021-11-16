package org.isaqb.onlineexam.mockexam.loader;

import java.util.Collection;
import java.util.Optional;

import org.isaqb.onlineexam.mockexam.DataConfiguration;
import org.isaqb.onlineexam.mockexam.DataConfiguration.UrlTemplateConfig;
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
                .toList();
            if (!tasks.isEmpty()) {
                taskMap.getTasks().put(topic, tasks);
            } else {
                System.err.printf("Task topic '%s' configured without any tasks.%n", topic);
            }
        }
        return taskMap;
    }

}
