package org.isaqb.onlineexam.mockexam;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.isaqb.onlineexam.mockexam.model.Language;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.experimental.Accessors;

@Component
// Map to all values of application.yaml
@ConfigurationProperties
@Data
@Accessors(chain = true)
public class DataConfiguration {

    // Map key "tasks" according to its name.
    // Keys without any matching field are simply ignored.
    private Map<String, QuizConfig> tasks;
    private Map<String, ExamConfig> exams;

    @Data
    public static class QuizConfig {
        private Map<Language, String> name;
        private List<UrlTemplateConfig> urls = new ArrayList<>();
        private List<String> refs;

        public boolean resolveRequires() {
            return refs != null && !refs.isEmpty();
        }
    }

    @Data
    public static class UrlTemplateConfig {
        // camelCase (urlTemplate) is mapped to snake-case (url-template)
        private String urlTemplate;
        private int from;
        private int to;
    }

    public Set<String> topics() {
        return tasks.keySet();
    }

    public Set<String> examNames() {
        return exams.keySet();
    }

    @Data
    public static class ExamConfig {
        private Map<Language, String> name;
        private int requiredPoints;
        private int maxTimeInMinutes;
        private List<String> taskRefs;
    }



    @PostConstruct
    public void resolve() {
        boolean someRefsResolved = false;
        var requiresResolve = configsWithRequiredResolves();
        while (!requiresResolve.isEmpty() && !someRefsResolved) {
            someRefsResolved = resolveRun(someRefsResolved, requiresResolve);
            requiresResolve = configsWithRequiredResolves();
        }
        assertAllRefererencesResolved();
    }

    private void assertAllRefererencesResolved() {
        var missing = new ArrayList<String>();
        for (var entry : tasks.entrySet()) {
            if (entry.getValue().resolveRequires()) {
                for (var ref : entry.getValue().getRefs()) {
                    missing.add(String.format("quiz::%s::%s", entry.getKey(), ref));
                }
            }
        }
        if (!missing.isEmpty()) {
            var msg = "Unresolvable configuration references:\n"
                    + missing.stream().collect(Collectors.joining("\n", "- ", ""));
            throw new RuntimeException(msg);
        }
    }

    private List<QuizConfig> configsWithRequiredResolves() {
        return tasks.values().stream().filter(QuizConfig::resolveRequires).toList();
    }

    private boolean resolveRun(boolean someRefsResolved, List<QuizConfig> requiresResolve) {
        for (var cfg : requiresResolve) {
            var it = cfg.getRefs().iterator();
            while (it.hasNext()) {
                String ref = it.next();
                var refTarget = tasks.get(ref);
                if (refTarget != null && !refTarget.resolveRequires()) {
                    someRefsResolved = true;
                    it.remove();
                    cfg.getUrls().addAll(refTarget.getUrls());
                }
            }
        }
        return someRefsResolved;
    }

}
