package org.isaqb.onlineexam.mockexam.ui;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.isaqb.onlineexam.mockexam.loader.AsciidocReader;
import org.isaqb.onlineexam.mockexam.model.Exam;
import org.isaqb.onlineexam.mockexam.model.Exam.Mode;
import org.isaqb.onlineexam.mockexam.model.Language;
import org.isaqb.onlineexam.mockexam.model.Option;
import org.isaqb.onlineexam.mockexam.model.Task;
import org.isaqb.onlineexam.mockexam.model.TaskAnswer;
import org.isaqb.onlineexam.mockexam.model.TaskType;
import org.isaqb.onlineexam.mockexam.model.calculation.CalculationResult;
import org.isaqb.onlineexam.mockexam.util.LanguageData;

import lombok.AllArgsConstructor;

public class UIData {

    private static LanguageData<Key,String> data = new LanguageData<>();
    static {
        data.put(Language.DE, Key.MENU_LABEL, "Frage %d (%d Punkte)");
        data.put(Language.EN, Key.MENU_LABEL, "Question %d (%d points)");
        data.put(Language.DE, Key.NEXT_LINK_LABEL, "Nächste Frage");
        data.put(Language.EN, Key.NEXT_LINK_LABEL, "Next question");
        data.put(Language.DE, Key.FIRST_LINK_LABEL, "Erste Frage");
        data.put(Language.EN, Key.FIRST_LINK_LABEL, "First question");
    }
    enum Key {
        MENU_LABEL,
        NEXT_LINK_LABEL,
        FIRST_LINK_LABEL
    }



    public Exam exam;
    private Language currentLanguage;
    public List<TaskWrapper> tasks;
    public CalculationResult result;



    public UIData(AsciidocReader adocReader, Exam exam, Language currentLanguage, List<TaskAnswer> givenAnswers, CalculationResult result) {
        this.exam = exam;
        this.currentLanguage = currentLanguage;
        this.result = result;
        this.tasks = initTaskWrappers(adocReader, givenAnswers);
    }

    private List<TaskWrapper> initTaskWrappers(AsciidocReader adocReader, List<TaskAnswer> givenAnswers) {
        AtomicInteger counter = new AtomicInteger(1);
        return exam.getTasks().stream()
            .map(t -> new TaskWrapper(
                t,
                counter.get(),
                data.get(currentLanguage, Key.MENU_LABEL),
                currentLanguage,
                findAnswerByNr(givenAnswers, counter.getAndIncrement()),
                result,
                adocReader.toHtml(t.getExplanation())
            ))
            .toList();
    }

    private TaskAnswer findAnswerByNr(List<TaskAnswer> list, int taskNumber) {
        return list.stream().filter(a->a.getTaskNumber()==taskNumber).findFirst().orElse(null);
    }

    public String statistik() {
        //TODO Sprache
        return String.format(
            "%.2f / %.2f Punkte (%.2f %%)<br>%s",
            result.totalPoints(), result.pointsMaximum, result.pointsRelative(),
            resultStatement()
        );
    }

    private String resultStatement() {
        if (exam.getMode() == Mode.QUIZ) {
            // You don't have to 'pass' a quiz.
            return "";
        } else {
            return result.passed
              ? "<result class=\"passed\">Sie haben bestanden.<result>"
              : "<result class=\"failed\">Es hat leider nicht gereicht.<result>";
        }
    }

    public String nextTaskLink(int currentTaskNumber) {
        return String.format(
            "<a href=\"javascript:changeToQuestion(%d)\">%s</a>",
            (currentTaskNumber % numberOfTasks()) + 1,
            currentTaskNumber == numberOfTasks()
                ? data.get(currentLanguage, Key.FIRST_LINK_LABEL)
                : data.get(currentLanguage, Key.NEXT_LINK_LABEL)
            );
    }

    public int numberOfTasks() {
        return tasks.size();
    }




    @AllArgsConstructor
    public static class TaskWrapper {
        public Task task;
        public int nr;
        private String menuLabelFormat;
        private Language currentLanguage;
        private TaskAnswer givenAnswer;
        private CalculationResult result;
        private String explanationHtml;


        //TODO: Im fertigen Produkt entfernen. Nur für Debug.
        public boolean debugShowArticle() {
            return nr == 4 || true;
        }


        public boolean hasExplanation() {
            return explanationHtml != null && !explanationHtml.isBlank();
        }

        public String getExplanation() {
            return explanationHtml;
        }

        public boolean flagSelected() {
            return givenAnswer != null && givenAnswer.isFlagged();
        }

        public boolean optionSelected(String position) {
            if (givenAnswer != null && givenAnswer.getOptionSelections().containsKey(position)) {
                var value = givenAnswer.getOptionSelections().get(position).get(0);
                return isTrue(value);
            } else {
                return false;
            }
        }

        public boolean optionSelected(Option option, int index) {
            String position = String.valueOf(option.getPosition());
            if (givenAnswer != null && givenAnswer.getOptionSelections().containsKey(position)) {
                return true;
            } else {
                return false;
            }
        }

        public boolean optionSelectedOLD(Option option, int index) {
            String position = String.valueOf(option.getPosition());
            if (givenAnswer != null && givenAnswer.getOptionSelections().containsKey(position)) {
                var valuesForThisOption = givenAnswer.getOptionSelections().get(position);
                var selected = valuesForThisOption.get(0);

                List<Boolean> columnBooleans = task.findOptionByPosition(option.getPosition())
                    .orElse(new Option())
                    .getColumnValues()
                    .stream()
                    .map(String::valueOf)
                    .map(this::isTrue)
                    .toList();

                var selectedBoolean = isTrue(selected);
                int idx = columnBooleans.indexOf(selectedBoolean);

                if (task.getType() == TaskType.CHOOSE) {
                    // with multiple columns we have to decide per column
                    return idx == index;
                } else {
                    // the only column could be selected or not
                    return isTrue(selected);
                }
            } else {
                // no answer given so nothing selected
                return false;
            }
        }

        private boolean isTrue(String value) {
            String lowerValue = value.toLowerCase();
            return lowerValue.contains("true") || lowerValue.contains("on") || lowerValue.equals("y");
        }

        public String getMenuLabel() {
            return String.format(menuLabelFormat, nr, task.getReachablePoints());
        }

        public String getMenuLabelForResultPage() {
            double maxPoints = task.getReachablePoints();
            String pointLabel = maxPoints < 2 ? "Punkt" : "Punkte";
            return String.format("Frage %d (%.2f / %.2f %s)", nr, actualPoints(), maxPoints, pointLabel); //TODO
        }

        private double actualPoints() {
            return result != null
                ? result.points.getOrDefault(task.getId(), 0.0)
                : 0;
        }

        public boolean missingPoints() {
            return actualPoints() < task.getReachablePoints();
        }

        public String getKind() {
            // TODO language
            if (task.getType() == TaskType.CHOOSE) {
                return String.format("%s-Frage: ordne zu", task.getType().type);
            } else {
                return String.format("%s-Frage: Wählen Sie %d Optionen aus", task.getType().type, task.countCorrectOptions());
            }
        }

        public String getReachablePoints() {
            // TODO language
            if (task.getReachablePoints() > 1) {
                return task.getReachablePoints() + " Punkte";
            } else {
                return task.getReachablePoints() + " Punkt";
            }
        }
    }

}
