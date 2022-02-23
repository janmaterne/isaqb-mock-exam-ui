package org.isaqb.onlinetrainer.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.isaqb.onlinetrainer.model.I18NText;
import org.isaqb.onlinetrainer.model.Language;
import org.isaqb.onlinetrainer.model.Option;
import org.isaqb.onlinetrainer.model.Task;
import org.isaqb.onlinetrainer.model.TaskType;
import org.springframework.stereotype.Component;

@Component
public class TaskParser {

    private Task task;
    private TagLanguage currentLanguage;
    private Option currentOption;
    private TaskParserState state;
    private int columnHeaderIndex = -1;
    private StringBuilder explanation;


    enum TagLanguage {
        DE, EN, NONE
    }

    private Optional<Language> currentLanguage() {
        switch (currentLanguage) {
        case DE: {
            return Optional.of(Language.DE);
        }
        case EN: {
            return Optional.of(Language.EN);
        }
        default:
            return Optional.empty();
        }
    }


    public Task parseADoc(String adocSource) {
        state = TaskParserState.START;
        task = new Task();
        task.setQuestion(new I18NText());
        Stream.of(adocSource.split("\n"))
            .filter( s -> !s.isEmpty() )
            .forEach(this::handleLine);
        mergeOptions();
        return task;
    }

    private void mergeOptions() {
        List<Option> list = new ArrayList<>();
        Map<Character, List<Option>> map = task.getPossibleOptions().stream()
            .collect(Collectors.groupingBy(Option::getPosition));
        for(List<Option> answers : map.values()) {
            Option merged = merge(answers);
            list.add(merged);
        }
        task.setPossibleOptions(list);
    }

    private Option merge(List<Option> options) {
        Option rv = new Option();
        for(Option a : options) {
            rv.setColumnValues(a.getColumnValues());
            rv.setPosition(a.getPosition());
            a.getText().getMap().entrySet().forEach(
                e -> rv.getText().addText(e.getKey(), e.getValue())
            );
        }
        return rv;
    }


    private void handleLine(String line) {
        if (line.matches("//\\s+tag::EXPLANATION.*")) {
            state = TaskParserState.EXPLANATION;
            explanation = new StringBuilder();
            return;
        }
        if (line.matches("//\\s+end::EXPLANATION.*")) {
            state = null;
            task.setExplanation(explanation.toString());
            return;
        }
        if (state == TaskParserState.EXPLANATION) {
            explanation.append(line).append(System.lineSeparator());
            return;
        }
        if (line.startsWith("|===")) {
            finishOption();
            return;
        }
        if (line.matches("//\s+tag::DE.*")) {
            currentLanguage = TagLanguage.DE;
            return;
        }
        if (line.matches("//\s+tag::EN.*")) {
            currentLanguage = TagLanguage.EN;
            return;
        }
        if (line.matches("\\*\\*ID:(.*)\\*")) {
            state = TaskParserState.POINTS;
            String id = substringBetween(line, "ID:", "**").trim();
            task.setId(id);
            return;
        }
        if (line.matches("\\|\\s*.-Frage.*")) {
            state = TaskParserState.TYPE;
            String kind = substringBetween(line, "|", "-").trim();
            task.setType(TaskType.of(kind));
            return;
        }
        if (line.matches("\\| .* Punkt.*")) {
            state = TaskParserState.POINTS;
            String points = substringBetween(line, "|", "Punkt").trim();
            task.setReachablePoints(Integer.parseInt(points));
            return;
        }
        if (line.matches("\\| .* point.*")) {
            // value already processed
            return;
        }
        if (line.matches("\\w.*")) {
            if (state == TaskParserState.ANSWER_TEXT || state == TaskParserState.ANSWER_POSITION) {
                currentLanguage().ifPresent( lang -> {
                    if (currentOption == null) {
                        currentOption = new Option();
                    }

                    var pattern = Pattern.compile(".\\|(.+)");
                    var matcher = pattern.matcher(line);
                    var text = matcher.matches()
                        ? matcher.group(1)
                        : line;

                    currentOption.getText().addText(lang, text.trim());
                });
            } else {
                state = TaskParserState.TEXT;
                currentLanguage().ifPresent( lang -> {
                    task.getQuestion().addText(lang, line.trim());
                });
            }
            return;
        }
        if (line.matches("\\| \\{[a-z]\\}.*")) {
            state = TaskParserState.ANSWER_CORRECT;
            finishOption();
            if (currentIsOptionComplete() || currentOption == null) {
                currentOption = new Option();
            }
            char choice = substringBetween(line, "{", "}").toLowerCase().charAt(0);
            currentOption.addColumnValue(choice);
            return;
        }
        if (line.matches("\\| \\([a-z]\\).*")) {
            state = TaskParserState.ANSWER_POSITION;
            char position = substringBetween(line, "(", ")").charAt(0);
            currentOption.setPosition(position);
            return;
        }
        if (line.matches("\\| .+")) {
            String text = line.replaceFirst("\\| (.+)", "$1");

            if (state == TaskParserState.TEXT && currentLanguage().isPresent()) {
                columnHeaderIndex++;
                task.addCurrentHeader(columnHeaderIndex, currentLanguage().get(), text.trim());
                return;
            }

            if (state == TaskParserState.ANSWER_CORRECT || state == TaskParserState.ANSWER_POSITION || state == TaskParserState.ANSWER_TEXT) {
                state = TaskParserState.ANSWER_TEXT;
                columnHeaderIndex = -1;
                currentLanguage().ifPresent( lang ->
                    currentOption.getText().addText(lang, text)
                );
                return;
            }
        }
    }


    private void finishOption() {
        if (currentIsOptionComplete()) {
            task.addPossibleOption(currentOption);
            currentOption = null;
        }
    }

    private boolean currentIsOptionComplete() {
        return currentOption != null
                && currentOption.getPosition() >= 'a'
                && currentOption.getPosition() <= 'z'
                && !currentOption.getText().isEmpty();
    }

    // Derived from Apache commons-lang3, so we dont need the full dependency.
    public static String substringBetween(final String str, final String open, final String close) {
        final int INDEX_NOT_FOUND = -1;
        final int start = str.indexOf(open);
        if (start != INDEX_NOT_FOUND) {
            final int end = str.indexOf(close, start + open.length());
            if (end != INDEX_NOT_FOUND) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }
}
