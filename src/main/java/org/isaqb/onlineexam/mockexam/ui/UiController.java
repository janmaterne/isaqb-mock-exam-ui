package org.isaqb.onlineexam.mockexam.ui;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.isaqb.onlineexam.mockexam.BuildInfo;
import org.isaqb.onlineexam.mockexam.QuizConfiguration;
import org.isaqb.onlineexam.mockexam.loader.AsciidocReader;
import org.isaqb.onlineexam.mockexam.loader.IntroductionLoader;
import org.isaqb.onlineexam.mockexam.model.Exam;
import org.isaqb.onlineexam.mockexam.model.I18NText;
import org.isaqb.onlineexam.mockexam.model.Language;
import org.isaqb.onlineexam.mockexam.model.TaskAnswer;
import org.isaqb.onlineexam.mockexam.model.calculation.Calculator;
import org.isaqb.onlineexam.mockexam.util.JsonMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Controller 
public class UiController {
	
	private Exam exam;
	private IntroductionLoader introductionLoader;
	private JsonMapper jsonMapper;
	private I18NText cookieDislaimer;
	private I18NText howToUse;
	private AutloadJS autoloadJS;
	private QuizConfiguration quizConfiguration;
	

	public UiController(
			Exam exam, 
			IntroductionLoader introductionLoader, 
			JsonMapper jsonMapper,
			AsciidocReader adocReader,
			@Value("classpath:messages/cookie-disclaimer.adoc") Resource resourceCookieDisclaimer,
			@Value("classpath:messages/how-to-use.adoc") Resource howToUse,
			AutloadJS autloadJS,
			QuizConfiguration quizConfiguration
	) throws IOException {
		this.exam = exam;
		this.introductionLoader = introductionLoader;
		this.jsonMapper = jsonMapper;
		this.cookieDislaimer = parseADoc(adocReader, resourceCookieDisclaimer);
		this.howToUse = parseADoc(adocReader, howToUse);
		this.autoloadJS = autloadJS;
		this.quizConfiguration = quizConfiguration;
	}



	private I18NText parseADoc(AsciidocReader adocReader, Resource resourceCookieDisclaimer) throws IOException {
		String adocCookieDisclaimer = IOUtils.toString(resourceCookieDisclaimer.getInputStream(), Charset.defaultCharset());
		return adocReader.parse(adocCookieDisclaimer);
	}



	@GetMapping("introduction.html")
	public String introduction(HttpServletResponse response, Model model, @RequestParam("language") String language) {
		response.addCookie(new Cookie("language", language));
		Language lang = Language.valueOf(language);
		
		model.addAttribute("html", introductionLoader.getHtml(lang));
		model.addAttribute("cookieDisclaimer", cookieDislaimer.getText(lang));
		model.addAttribute("howToUse", howToUse.getText(lang));
		model.addAttribute("appversion", String.format("Version %s - Build %s", BuildInfo.getVersion(), BuildInfo.getBuildTimestamp()));
		model.addAttribute("quizOptions", possibleQuizOptions(lang));
		autoloadJS.injectAutoReloadJS(model);
		
		return "introduction.html";
	}

	private List<QuizOptions> possibleQuizOptions(Language lang) {
		return quizConfiguration.getQuiz().entrySet().stream()
			.map( e -> new QuizOptions(e.getKey(), e.getValue().getName().get(lang)) )
			.toList();
	}



	@GetMapping("process-exam.html")
	public String processExam(
			Model model, 
			@RequestParam String language,
			@RequestParam String mode,
			@CookieValue(name = "language", required = false) String languageFromCookie, 
			@CookieValue(name = "quizmode", required = false) String quizmodeFromCookie,
			@CookieValue(name = "givenAnswers", required = false) String givenAnswersJson
	) {
		String quizmode = cookieOverParameter(quizmodeFromCookie, mode);
		String lang = cookieOverParameter(languageFromCookie, language);
		List<TaskAnswer> givenAnswers = givenAnswersFromCookie(givenAnswersJson);

		UIData uiData = new UIData(exam, Language.valueOf(languageFromCookie), givenAnswers, null);
		model.addAttribute("exam", exam);
		model.addAttribute("util", uiData);
		model.addAttribute("givenAnswers", givenAnswers);
		model.addAttribute("language", lang);
		autoloadJS.injectAutoReloadJS(model);
		
		return "process-exam.html";
	}
	
	private String cookieOverParameter(String cookieValue, String paramValue) {
		return cookieValue != null ? cookieValue : paramValue;
	}
	


	private List<TaskAnswer> givenAnswersFromCookie(String givenAnswersJson) {
		return givenAnswersJson == null
			? Collections.emptyList()
			: jsonMapper.fromStringToAnswers(givenAnswersJson);
	}
	 
	
	
	@PostMapping(value="send-exam")
	public String sendExam(HttpServletResponse response, Model model, @RequestBody(required =false) MultiValueMap<String, String> formData) {
		Collection<TaskAnswer> givenAnswers = parse(formData);
		model.addAttribute("givenAnswers", givenAnswers);
		response.addCookie(new Cookie("givenAnswers", jsonMapper.toString(givenAnswers)));
		autoloadJS.injectAutoReloadJS(model);
		return answersMissing(givenAnswers) ? "missing-tasks.html" : "redirect:/calculatePoints";
	}

	private boolean answersMissing(Collection<TaskAnswer> givenAnswers) {
		return givenAnswers.stream().filter(a -> !a.getOptionSelections().isEmpty()).count() < exam.getTasks().size();
	}

	private Collection<TaskAnswer> parse(MultiValueMap<String, String> formData) {
		if (formData == null) {
			return Collections.emptyList();
		}
		
		Map<Integer, TaskAnswer> answers = new HashMap<Integer, TaskAnswer>();
		
		for(Entry<String, List<String>> entry : formData.entrySet()) {
			String[] parts = entry.getKey().split("-");
			if ("frage".equals(parts[0])) {
				int nr = Integer.parseInt(parts[1]);
				String option = parts[2];
				var answer = answers.computeIfAbsent(nr, x -> new TaskAnswer(x));
				answer.put(option, entry.getValue());
				
			} 
			if ("flag".equals(parts[0])) {
				int nr = Integer.parseInt(parts[1]);
				boolean flagged = entry.getValue().contains("on");
				var answer = answers.computeIfAbsent(nr, x -> new TaskAnswer(x));
				answer.setFlagged(flagged);
			}
		}
		
		return answers.values();
	}

	
	
	@GetMapping("calculatePoints")
	public String calculatePoints(HttpServletResponse response, Model model, @CookieValue("language") String language, @CookieValue(name = "givenAnswers", required = false) String givenAnswersJsonBase64) {
		List<TaskAnswer> givenAnswers = givenAnswersFromCookie(givenAnswersJsonBase64);

		Calculator calc = new Calculator();
		var result = calc.calculate(exam, givenAnswers);
		model.addAttribute("result", result);
		response.addCookie(new Cookie("result", jsonMapper.toString(result)));
		
		UIData uiData = new UIData(exam, Language.valueOf(language), givenAnswers, result);
		model.addAttribute("util", uiData);
		
		autoloadJS.injectAutoReloadJS(model);
		return "result.html";
	}
	
	
	
	@GetMapping("result-details.html")
	public String resultDetails(Model model, @CookieValue("language") String language, @CookieValue(name = "givenAnswers", required = false) String givenAnswersJsonBase64, @CookieValue("result") String resultJsonBase64) {
		List<TaskAnswer> givenAnswers = givenAnswersFromCookie(givenAnswersJsonBase64);
		
		var result = jsonMapper.fromStringToCalculationResult(resultJsonBase64);
		model.addAttribute("result", result);
		
		UIData uiData = new UIData(exam, Language.valueOf(language), givenAnswers, result);
		model.addAttribute("util", uiData);
		
		autoloadJS.injectAutoReloadJS(model);
		return "result-details.html";
	}
	
	
	
	@GetMapping("end")
	public String end(HttpServletResponse response, HttpServletRequest request) {
		deleteCookies(response, request);
		return "index.html";
	}

	private void deleteCookies(HttpServletResponse response, HttpServletRequest request) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for(Cookie cookie : cookies) {
				// bandwidth
				cookie.setValue("");
				cookie.setMaxAge(0);
				response.addCookie(cookie);
			}
		}
	}

}
