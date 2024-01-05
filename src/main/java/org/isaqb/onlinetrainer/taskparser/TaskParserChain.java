package org.isaqb.onlinetrainer.taskparser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.isaqb.onlinetrainer.model.Task;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TaskParserChain implements TaskParser {

    private List<TaskParser> chain = new ArrayList<>();

    public TaskParserChain(TaskParser... parsers) {
        chain.addAll(Arrays.asList(parsers));
    }

    @Override
    public Task parseContent(String content) {
        var exceptions = new ArrayList<Exception>();
        for(var parser : chain) {
            try {
                return parser.parseContent(content);
            } catch (Exception e) {
                exceptions.add(e);
            }
        }
        if (!exceptions.isEmpty()) {
            for(var ex : exceptions) {
            	log.error(ex.getLocalizedMessage(), ex);
            }
        }
        return null;
    }

}
