package org.isaqb.onlinetrainer.statistics;

import org.isaqb.onlinetrainer.model.Exam;
import org.isaqb.onlinetrainer.model.Exam.Mode;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class StatisticService {

    private StatisticRepository repository;

    public void processed(Exam exam) {
        String key = exam.getMode() == Mode.EXAM
            ? "exam." + exam.getName()
            : "quiz";
        save(key);
    }

    private void save(String key) {
        repository.save(
            repository.findById(key)
                .orElse(new Statistic(key, 0))
                .increase()
        );
    }

}
