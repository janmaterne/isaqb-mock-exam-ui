package org.isaqb.onlinetrainer.service;

import java.util.List;

import org.isaqb.onlinetrainer.DataConfiguration;
import org.isaqb.onlinetrainer.model.Language;
import org.isaqb.onlinetrainer.ui.QuizOptions;
import org.springframework.stereotype.Service;

@Service
public class TrainerService {

    private DataConfiguration quizConfiguration;

	public TrainerService(DataConfiguration quizConfiguration) {
		this.quizConfiguration = quizConfiguration;
	}

	public List<QuizOptions> possibleQuizOptions(Language lang) {
        return quizConfiguration.getTasks().entrySet().stream()
    		.filter(e -> !e.getValue().isSkip())
            .map(e -> new QuizOptions(e.getKey(), e.getValue().getName().get(lang)))
            .toList();
    }

	public List<QuizOptions> possibleExams(Language lang) {
        return quizConfiguration.getExams().entrySet().stream()
            .map(e -> new QuizOptions(e.getKey(), e.getValue().getName().get(lang)))
            .toList();
	}
}
