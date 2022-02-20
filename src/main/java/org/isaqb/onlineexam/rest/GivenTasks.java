package org.isaqb.onlineexam.rest;

import java.util.List;

public record GivenTasks(double requiredPoints, List<TaskData> tasks) {
}
