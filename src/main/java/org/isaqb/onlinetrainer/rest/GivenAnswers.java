package org.isaqb.onlinetrainer.rest;

import java.util.List;

import org.isaqb.onlinetrainer.rest.GivenAnswers.AnsweredTask;


public record GivenAnswers(List<String> questionIds, List<AnsweredTask> options) {
    public record AnsweredTask(String questionId, List<Character> selectedOptions) {}
}
