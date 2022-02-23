package org.isaqb.onlinetrainer.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class TaskMap {
    Map<String, List<Task>> tasks = new HashMap<>();
}
