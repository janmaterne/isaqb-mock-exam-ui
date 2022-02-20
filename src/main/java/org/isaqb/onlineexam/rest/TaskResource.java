package org.isaqb.onlineexam.rest;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.isaqb.onlineexam.BuildInfo;
import org.isaqb.onlineexam.calculation.Calculator;
import org.isaqb.onlineexam.loader.AsciidocReader;
import org.isaqb.onlineexam.loader.IntroductionLoader;
import org.isaqb.onlineexam.model.Exam;
import org.isaqb.onlineexam.model.Exam.Mode;
import org.isaqb.onlineexam.model.ExamFactory;
import org.isaqb.onlineexam.model.I18NText;
import org.isaqb.onlineexam.model.Language;
import org.isaqb.onlineexam.model.Task;
import org.isaqb.onlineexam.model.TaskAnswer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api")
public class TaskResource {

    private IntroductionLoader introductionLoader;
    private I18NText howToUse;
    private ExamFactory examFactory;
    private Calculator calculator;



    public TaskResource(
        IntroductionLoader introductionLoader,
        ExamFactory examFactory,
        @Value("classpath:messages/how-to-use.adoc") Resource howToUse,
        AsciidocReader adocReader,
        Calculator calculator
    ) {
        this.introductionLoader = introductionLoader;
        this.examFactory = examFactory;
        this.howToUse = adocReader.parse(howToUse);
        this.calculator = calculator;
    }



    @GetMapping
    public List<String> supportedLanguages() {
        return Stream.of(Language.values()).map(Language::name).toList();
    }



    @GetMapping("introduction")
    public IntroductionData introduction(@RequestParam String language) {
        Language lang = Language.valueOf(language.toUpperCase());
        Objects.requireNonNull(lang, () -> "Valid language required. Available: " + this.supportedLanguages());
        return new IntroductionData(
            introductionLoader.getHtml(lang),
            howToUse.getText(lang),
            BuildInfo.getVersion(),
            BuildInfo.getBuildTimestamp()
        );
    }



    @GetMapping("start")
    public GivenTasks start(
        @RequestParam(required = false) String name,
        @RequestParam(required = false) String questionIds,
        @RequestParam(required = false) String topics
    ) {
        Exam exam = name != null && !name.isBlank()
            ? examFactory.examByName(name)
            : examFactory.from(Mode.QUIZ, questionIds, topics);
        return from(exam);
    }

    private GivenTasks from(Exam exam) {
        var tasks = exam.getTasks().stream().map(this::toTaskData).toList();
        return new GivenTasks(exam.getRequiredPoints(), tasks);
    }

    private TaskData toTaskData(Task task) {
        var possibleOptions = task.getPossibleOptions().stream().collect(Collectors.toMap(o->o.getPosition(), o->o.getText()));
        return new TaskData(task.getId(), task.getType(), task.getReachablePoints(), task.getQuestion(), task.getExplanation(), task.getColumnHeaders(), possibleOptions);
    }



    @PostMapping
    public Result process(@RequestBody GivenAnswers answer) {
        System.out.printf("%n%n%nprocess()%n%s%n%n%n%n", answer);
        Exam exam = examFactory.examByQuestionIds(answer.questionIds());
        var line = "=============================================";
        
        var calcResult = calculator.calculate(exam, answersForCalculator(answer));
        System.out.printf("%s%n%s%n%s%n%s%n", line, exam, calcResult, line);
        return new Result(calcResult.passed, calcResult.pointsMaximum, calcResult.totalPoints(), calcResult.points);
    }

	private List<TaskAnswer> answersForCalculator(GivenAnswers answer) {
		return answer.options().stream()
			.map( a -> new TaskAnswer() )
			.toList();
	}

}
