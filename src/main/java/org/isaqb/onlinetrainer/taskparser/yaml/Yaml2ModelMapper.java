package org.isaqb.onlinetrainer.taskparser.yaml;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.isaqb.onlinetrainer.model.I18NText;
import org.isaqb.onlinetrainer.model.Language;
import org.isaqb.onlinetrainer.model.Task;
import org.isaqb.onlinetrainer.model.TaskType;
import org.mapstruct.Mapper;

@Mapper
public interface Yaml2ModelMapper {

    Task mapTask(YamlTask yaml);

    // TaskType.valueOf(String) or of(String) - specify in a type safe manner.
    default TaskType mapTaskType(String value) {
        return TaskType.of(value);
    }

    /**
     * <p>Transform the column-based structure to row-based structure.</p>
     *
     * <b>adoc:</b>
     * <pre>
     * columnHeaders:
     *     DE:
     *       - a
     *       - b
     *     EN:
     *       - c
     *       - d
     * </pre>
     *
     * <b>YamlTask:</b>
     * <pre>
     * { DE: [a,b], EN: [c,d] }
     * </pre>
     *
     * <b>Task:</b>
     * <pre>
     * [ {DE:a, EN:c}, {DE:b, EN:d} ]
     * </pre>
     */
    default List<I18NText> mapToList(Map<Language,List<String>> value) {
        if (value == null) {
            return Collections.emptyList();
        }

        var list = new ArrayList<I18NText>();
        int size = value.values().iterator().next().size();
        for (int i = 0; i < size; i++) {
            var text = new I18NText();
            for(var lang : value.keySet()) {
                text.addText(lang, value.get(lang).get(i));
            }
            list.add(text);
        }
        return list;
    }

    default I18NText mapToText(Map<Language,String> value) {
        var text = new I18NText();
        value.entrySet().forEach(e -> text.addText(e.getKey(), e.getValue()));
        return text;
    }

    // comma-separated list of characters
    default List<Character> mapColumnValues(String value) {
        return Stream.of(value.split(","))
                .map( s -> s.charAt(0) )
                .toList();
    }

}