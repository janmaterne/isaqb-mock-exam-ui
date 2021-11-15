package org.isaqb.onlineexam.mockexam.loader;

import org.isaqb.onlineexam.mockexam.DataConfiguration;
import org.isaqb.onlineexam.mockexam.model.TaskMap;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class TaskMapLoader {

    private DataConfiguration config;
    
    @Bean
    public TaskMap loadTasks() {
        //TODO
        return new TaskMap();
    }
    
}
