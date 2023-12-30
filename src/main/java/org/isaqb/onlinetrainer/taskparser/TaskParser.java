package org.isaqb.onlinetrainer.taskparser;

import org.isaqb.onlinetrainer.model.Task;

public interface TaskParser {

    Task parseContent(String content);

}