package org.isaqb.onlinetrainer.taskparser.yaml;

import org.isaqb.onlinetrainer.model.Task;
import org.isaqb.onlinetrainer.taskparser.TaskParser;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class YamlTaskParser implements TaskParser {

    private final Yaml2ModelMapper mapper;

    @Override
    public Task parseContent(String content) {
        Yaml yaml = new Yaml(new Constructor(YamlTask.class, new LoaderOptions()));
        var task = (YamlTask)yaml.load(content);
        return mapper.mapTask(task);
    }

}
