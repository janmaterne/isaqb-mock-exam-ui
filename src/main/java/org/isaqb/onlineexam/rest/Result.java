package org.isaqb.onlineexam.rest;

import java.util.Map;

public record Result(boolean passed, double pointsMaximum, double totalPoints, Map<String, Double> pointsPerQuestion) {
}
