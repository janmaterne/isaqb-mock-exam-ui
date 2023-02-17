package org.isaqb.onlinetrainer.model;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.isaqb.onlinetrainer.DataConfiguration;
import org.isaqb.onlinetrainer.DataConfiguration.ExamConfig;
import org.isaqb.onlinetrainer.model.Exam.Mode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ExamFactory {

    public static final String ALL_TOPIC = "-all";

    private Random random = new SecureRandom(UUID.randomUUID().toString().getBytes());
    private int quizmodeMaxNumbersOfQuestions;
    private Map<String, List<Task>> tasks;
    private Map<String, ExamConfig> exams;


    public ExamFactory(
            @Value("${quizmode.max-numbers-of-questions}")
            int quizmodeMaxNumbersOfQuestions,
            DataConfiguration config,
            TaskMap taskMap
    ) {
        this.quizmodeMaxNumbersOfQuestions = quizmodeMaxNumbersOfQuestions;
        this.exams = config.getExams();
        this.tasks = taskMap.getTasks();
    }

    protected static ExamFactory testInstance(
            int quizmodeMaxNumbersOfQuestions,
            Map<String, ExamConfig> exams,
            Map<String, List<Task>> tasks
    ) {
        return new ExamFactory(
            quizmodeMaxNumbersOfQuestions,
            new DataConfiguration().setExams(exams),
            new TaskMap().setTasks(tasks)
        );
    }



    public Exam from(Mode mode, String questionIdsCommaSeparated, String topicsCommaSeparated) {
        List<String> questionIds = questionIdsCommaSeparated == null ? new ArrayList<>() : List.of(questionIdsCommaSeparated.split(","));
        List<String> topics = topicsCommaSeparated == null ? new ArrayList<>() : List.of(topicsCommaSeparated.split(","));
        return from(mode, questionIds, topics);
    }

    public Exam from(Mode mode, List<String> questionIds, List<String> topics) {
        if (mode == Mode.QUIZ) {
            if (questionIds.isEmpty()) {
                return examByTopics(topics).setMode(mode);
            } else {
                return examByQuestionIds(questionIds).setMode(mode);
            }
        } else {
            if (topics.isEmpty()) {
                return mockExam().setMode(mode);
            }
            if (topics.size() != 1) {
                throw new RuntimeException("You could only select 1 topic for an exam!");
            }
            String name = topics.get(0);
            return examByName(name).setMode(mode);
        }
    }



    public Exam mockExam() {
        return examByName("mock");
    }

    public Exam examByName(String name) {
        ExamConfig examConfig = exams.get(name);
        if (examConfig != null) {
            var taskRefs = examConfig.getTaskRefs();
            List<Task> examTasks = new ArrayList<>();
            taskRefs.forEach( ref -> examTasks.addAll(tasks.get(ref)));
            return Exam.createExam(examConfig.getRequiredPoints(), examTasks).setName(name);
        } else {
        	throw new RuntimeException(String.format("Kein Examen mit Namen '%s' gefunden.", name));
        }
    }



    public Exam examByQuestionIds(String questionIdsSeparatedByComma) {
        Objects.requireNonNull(questionIdsSeparatedByComma, "comma-separated ID-List required");
        return examByQuestionIds(Arrays.asList(questionIdsSeparatedByComma.split(",")));
    }

    public Exam examByQuestionIds(List<String> questionsIds) {
        var allTasks = allTasks();
        var examTasks = questionsIds.stream()
            .map(allTasks::get)
            .filter(Objects::nonNull)
            .toList();
        return Exam.createQuiz(examTasks);
    }



    public Exam examByTopics(String topics) {
        return examByTopics(Arrays.asList(topics.split(",")));
    }

    public Exam examByTopics(List<String> topics) {
        List<Task> possibleTasks;
        if (topics.isEmpty() || topics.contains(ALL_TOPIC)) {
            possibleTasks = allTasks().values().stream().toList();
        } else {
            possibleTasks = new ArrayList<Task>();
            topics.forEach( topic -> possibleTasks.addAll(tasks.getOrDefault(topic, new ArrayList<>())));
        }
        List<Task> randomTasks = randomElements(possibleTasks, quizmodeMaxNumbersOfQuestions);
        return Exam.createQuiz(randomTasks);
    }

    public Exam examByTopic(String topic) {
        if (ALL_TOPIC.equalsIgnoreCase(topic)) {
            List<Task> allTasks = new ArrayList<>(allTasks().values());
            List<Task> randomTasks = randomElements(allTasks, quizmodeMaxNumbersOfQuestions);
            return Exam.createQuiz(randomTasks);
        } else {
            List<Task> randomTasks = randomElements(tasks.getOrDefault(topic, new ArrayList<>()), quizmodeMaxNumbersOfQuestions);
            return Exam.createQuiz(randomTasks);
        }
    }



    private Map<String, Task> allTasks() {
        return tasks.values().stream()
            // flatten Stream<List<Task>> to Stream<Task>
            .flatMap(Collection::stream)
            // create a Map with the "first value wins"
            .collect(Collectors.toMap(Task::getId, Function.identity(), (t1,t2)->t1));
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
