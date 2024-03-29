package org.isaqb.onlinetrainer.ui;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.isaqb.onlinetrainer.model.Exam;
import org.isaqb.onlinetrainer.model.Exam.Mode;
import org.isaqb.onlinetrainer.model.ExamFactory;
import org.isaqb.onlinetrainer.model.Task;
import org.isaqb.onlinetrainer.util.Base64Handler;
import org.isaqb.onlinetrainer.util.CookieHelper;
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
        String modeAsString = getFromRequest(request, KEY_MODE).orElse("quiz");
        Mode mode = Mode.of(modeAsString);
        List<String> questionIds = getListFromRequest(request, KEY_QUESTIONIDS);
        List<String> topics = getListFromRequest(request, KEY_TOPICS);
        if (topics.isEmpty()) {
            topics = parseTopicsFromQueryString(request);
        }
        return factory.from(mode, trail(questionIds), trail(topics));
    }

    private List<String> trail(List<String> list) {
        return list.stream().filter(s->!s.isBlank()).toList();
    }

    private List<String> parseTopicsFromQueryString(HttpServletRequest request) {
        String prefix = "topic-";
        return request.getParameterMap().keySet().stream()
            .filter( e -> e.startsWith(prefix) )
            .map( s -> s.substring(prefix.length()) )
            .distinct()
            .toList();
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
