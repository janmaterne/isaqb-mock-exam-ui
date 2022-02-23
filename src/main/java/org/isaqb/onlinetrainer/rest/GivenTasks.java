package org.isaqb.onlinetrainer.rest;

import java.util.List;

public record GivenTasks(double requiredPoints, List<TaskData> tasks) {
}
