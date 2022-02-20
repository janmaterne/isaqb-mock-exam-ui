package org.isaqb.onlineexam.rest;

import java.util.List;

import org.isaqb.onlineexam.rest.GivenAnswers.AnsweredTask;


public record GivenAnswers(List<String> questionIds, List<AnsweredTask> options) {
    public record AnsweredTask(String questionId, List<Character> selectedOptions) {}
}
