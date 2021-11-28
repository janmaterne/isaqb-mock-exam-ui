package org.isaqb.onlineexam.mockexam.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Task {

    private String id;
    private TaskType type;
    private int reachablePoints;
    private I18NText question;
    private String explanation;

    /**
     * Especially for K-questions we also have headers per columns, like
     * <tt>appropriate</tt> and <tt>not appropriate</tt>.
     */
    private List<I18NText> columnHeaders = new ArrayList<>();

    private List<Option> possibleOptions = new ArrayList<>();



    public Task(String id, TaskType type, int reachablePoints, I18NText question) {
        this.id = id;
        this.type = type;
        this.reachablePoints = reachablePoints;
        this.question = question;
    }



    public List<String> columnValues() {
        return IntStream.iterate(0,  i -> i<columnHeaders.size(), i->i+1)
            .mapToObj(String::valueOf)
            .toList();
    }

    public Task addPossibleOption(Option option) {
        possibleOptions.add(option);
        return this;
    }

    public Task addCurrentHeader(int index, Language language, String text) {
        // size(1-based) vs. index(0-based)
        while(columnHeaders.size()-1 < index) {
            columnHeaders.add(new I18NText());
        }
        columnHeaders.get(index).addText(language, text);
        return this;
    }

    public String getCurrentHeader(int index, Language language) {
        return columnHeaders.get(index).getText(language);
    }

    public long countCorrectOptions() {
        return possibleOptions.stream().filter( opt -> !opt.getCorrectColumnsIndices().isEmpty() ).count();
    }

    public Optional<Option> findOptionByPosition(char position) {
        return possibleOptions.stream()
            .filter(opt -> opt.getPosition() == position)
            .findFirst();
    }
}
