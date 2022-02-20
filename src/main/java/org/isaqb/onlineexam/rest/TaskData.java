package org.isaqb.onlineexam.rest;

import java.util.List;
import java.util.Map;

import org.isaqb.onlineexam.model.I18NText;
import org.isaqb.onlineexam.model.TaskType;

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
