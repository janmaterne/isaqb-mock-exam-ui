package org.isaqb.onlineexam.mockexam.ui;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.isaqb.onlineexam.mockexam.model.Exam;
import org.isaqb.onlineexam.mockexam.model.Exam.Mode;
import org.isaqb.onlineexam.mockexam.model.ExamFactory;
import org.isaqb.onlineexam.mockexam.model.Task;
import org.isaqb.onlineexam.mockexam.util.Base64Handler;
import org.isaqb.onlineexam.mockexam.util.CookieHelper;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class ExamHttpAdapter {

    private static final String KEY_QUESTIONIDS = "questionIds";
    private static final String KEY_TOPICS = "topics";
    private static final String KEY_MODE = "mode";



    private ExamFactory factory;
    private Base64Handler base64;



    public Exam from(HttpServletRequest request) {
        Optional<String> fromRequest = getFromRequest(request, KEY_MODE);
        String orElse = fromRequest.orElse("quiz");
        Mode mode = Mode.of(orElse);
        List<String> questionIds = getListFromRequest(request, KEY_QUESTIONIDS);
        List<String> topics = getListFromRequest(request, KEY_TOPICS);

        System.out.println("from(request)");
        System.out.printf("- req: %s%n", fromRequest);
        System.out.printf("- orElse: %s%n", orElse);
        System.out.printf("- mode: %s%n", mode);
        System.out.printf("- questionIds: %s%n", questionIds);
        System.out.printf("- topics: %s%n", topics);

        return from(mode, trail(questionIds), trail(topics));
    }

    private List<String> trail(List<String> list) {
        return list.stream().filter(s->!s.isBlank()).toList();
    }

    private Exam from(Mode mode, List<String> questionIds, List<String> topics) {
        if (mode == Mode.QUIZ) {
            if (questionIds.isEmpty()) {
                return factory.examByTopics(topics).setMode(mode);
            } else {
                return factory.examByQuestionIds(questionIds).setMode(mode);
            }
        } else {
            System.out.printf("- topics.size: %s - %s%n", topics.size(), topics);
            topics.forEach(t->System.out.printf("  -- '%s'%n", t));
            if (topics.isEmpty()) {
                return factory.mockExam().setMode(mode);
            }
            if (topics.size() != 1) {
                throw new RuntimeException("You could only select 1 topic for an exam!");
            }
            return factory.examByTopic(topics.get(0)).setMode(mode);
        }
    }



    private List<String> getListFromRequest(HttpServletRequest request, String key) {
        return Arrays.asList(
            getFromRequest(request, key).orElse("").split(",")
        ).stream()
            .filter(s->!s.isBlank())
            .toList();
    }

    private Optional<String> getFromRequest(HttpServletRequest request, String key) {
        return getCookieValue(request, key)
            .or( () -> getParamValue(request, key) );
    }



    private Optional<String> getParamValue(HttpServletRequest request, String key) {
        return Optional.ofNullable(request.getParameter(key));
    }

    private Optional<String> getCookieValue(HttpServletRequest request, String key) {
        return new CookieHelper(request, null, base64).readCookie(key);
    }



    public void send(HttpServletResponse response, Exam exam) {
        String questionIds = exam.getTasks().stream().map(Task::getId).collect(Collectors.joining(","));
        CookieHelper cookieHelper = new CookieHelper(null, response, base64);
        cookieHelper.setCookie(KEY_QUESTIONIDS, questionIds);
        cookieHelper.setCookie(KEY_MODE, exam.getMode().name());
    }

}
