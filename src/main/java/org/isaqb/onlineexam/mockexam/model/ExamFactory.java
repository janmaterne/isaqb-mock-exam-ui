package org.isaqb.onlineexam.mockexam.model;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ExamFactory {

    private static final String ALL_TOPIC = "-all";
    
    private Random random = new SecureRandom(UUID.randomUUID().toString().getBytes());

    int quizmodeMaxNumbersOfQuestions;
    Exam mockExam;
    Map<String, List<Task>> tasks;

    public ExamFactory(
            @Value("quizmode.max-numbers-of-questions")
            int quizmodeMaxNumbersOfQuestions,
            Exam mockExam,
            Map<String, List<Task>> tasks
    ) {
        this.quizmodeMaxNumbersOfQuestions = quizmodeMaxNumbersOfQuestions;
        this.mockExam = mockExam;
        this.tasks = tasks;
    }



    public Exam examByRequest(String quizTopic, String questionIds) {
        if (questionIds != null) {
            return examByQuestionIds(questionIds);
        } else {
            return quizTopic == null ? mockExam() : examByTopic(quizTopic);
        }
    }

    public Exam mockExam() {
        return mockExam;
    }

    public Exam examByQuestionIds(String questionIdsSeparatedByComma) {
        Objects.requireNonNull(questionIdsSeparatedByComma, "comma-separated ID-List required");
        return examByQuestionIds(questionIdsSeparatedByComma.split(","));
    }

    public Exam examByQuestionIds(String... questionsIds) {
        Objects.requireNonNull(questionsIds, "ID-List required");
        var ids = Arrays.asList(questionsIds);
        var examTasks = allTasks().stream()
            .filter( t -> ids.contains(t.getId()) )
            .toList();
        return new Exam(0, examTasks);
    }

    public Exam examByTopic(String topic) {
        if (ALL_TOPIC.equalsIgnoreCase(topic)) {
            List<Task> allTasks = allTasks();
            List<Task> randomTasks = randomElements(allTasks, quizmodeMaxNumbersOfQuestions);
            return new Exam(0, randomTasks);
        } else {
            List<Task> randomTasks = randomElements(tasks.getOrDefault(topic, new ArrayList<>()), quizmodeMaxNumbersOfQuestions);
            return new Exam(0, randomTasks);
        }
    }



    private List<Task> allTasks() {
        List<Task> allTasks = new ArrayList<>();
        allTasks.addAll(mockExam.getTasks());
        tasks.values().forEach(allTasks::addAll);
        return allTasks;
    }

    private <T> List<T> randomElements(List<T> list, int numberOfElements) {
        if (numberOfElements >= list.size()) {
            return list;
        } else {
            var possible = new ArrayList<T>(list);
            var rv = new ArrayList<T>();
            for(int i=0; i<numberOfElements; i++) {
                int index = random.nextInt(possible.size());
                rv.add(possible.get(index));
                possible.remove(index);
            }
            return rv;
        }
    }

}
