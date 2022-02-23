package org.isaqb.onlinetrainer.rest;

import java.util.List;
import java.util.Map;

import org.isaqb.onlinetrainer.model.I18NText;
import org.isaqb.onlinetrainer.model.TaskType;

public record TaskData(
    String id,
    TaskType type,
    int reachablePoints,
    I18NText question,
    String explanation,
    List<I18NText> columnHeaders,
    Map<Character,I18NText> possibleOptions
) {
}
