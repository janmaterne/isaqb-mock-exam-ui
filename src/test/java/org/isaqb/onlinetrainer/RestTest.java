package org.isaqb.onlinetrainer;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import org.isaqb.onlinetrainer.rest.GivenAnswers;
import org.isaqb.onlinetrainer.rest.GivenAnswers.AnsweredTask;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

@Disabled("Not ready for public tests")
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
@AutoConfigureMockMvc
public class RestTest {

    @Autowired
    private MockMvc mockMvc;


    @Nested
    class SupportedLanguages {
        @Test
        void twoLanguagesSupported() throws Exception {
            request()
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$", hasItem("EN")));
        }

        private ResultActions request() throws Exception {
            return mockMvc.perform(get("/api/").contentType(MediaType.APPLICATION_JSON));
        }
    }


    @Nested
    class Introduction {
        @Test
        void containsMetaData() throws Exception {
            request("DE")
                .andExpect(jsonPath("appVersion", is(BuildInfo.getVersion())))
                .andExpect(jsonPath("buildTStamp", is(BuildInfo.getBuildTimestamp())));
        }

        @Test
        void noLanguage() throws Exception {
            request(null)
//                .andDo(print())
                .andExpect(status().is(400))
                .andExpect(status().reason(containsString("language")));
        }

        @Test
        void german() throws Exception {
            request("DE")
                .andExpect(jsonPath("introduction", containsString("Veranschaulichung")));
        }

        @Test
        void english() throws Exception {
            request("EN")
                .andExpect(jsonPath("introduction", containsString("Explanations")));
        }

        private ResultActions request(String language) throws Exception {
            MockHttpServletRequestBuilder request = get("/api/introduction").contentType(MediaType.APPLICATION_JSON);
            if (language != null) {
                request.param("language", language);
            }
            return mockMvc.perform(request);
        }
    }


    @Nested
    class Start {
        @Test
        void differentQuizzesPerCall() throws Exception {
            List<TreeSet<String>> results = new ArrayList<>();
            for(int i=0; i<10; i++) {
                results.add(new TreeSet<>(questionIdsForFoundationQuiz()));
            }
            assertAllDifferent(results);
        }

        private void assertAllDifferent(List<TreeSet<String>> results) {
            for(int compareIndex = 0; compareIndex < results.size()-1; compareIndex++) {
                for(int withIndex = compareIndex+1; withIndex < results.size(); withIndex++) {
                    assertNotEquals(results.get(compareIndex).toString(), results.get(withIndex).toString());
                }
            }
        }

        private List<String> questionIdsForFoundationQuiz() throws Exception {
            String json = request(null, null, "foundation").andReturn().getResponse().getContentAsString();
            return filter(json, "$..id");
        }

        @Test
        void mockExam() throws Exception {
            request("mock", null, null)
                .andDo(print())
                .andExpect(jsonPath("$.requiredPoints", is(30.0)))
                .andExpect(jsonPath("$.tasks", hasSize(39)))
                .andExpect(jsonPath("$..id", hasItem("Q-20-04-01")));
        }

        @Test
        void returnSameIfNameIsSame() throws Exception {
            var first = request("mock", null, null).andReturn().getResponse().getContentAsString();
            for(int i=0; i<10; i++) {
                assertEquals(first, request("mock", null, null).andReturn().getResponse().getContentAsString());
            }
        }

        private ResultActions request(String name, String questionIds, String topics) throws Exception {
            MockHttpServletRequestBuilder request = get("/api/start").contentType(MediaType.APPLICATION_JSON);
            if (name != null) {
                request.param("name", name);
            }
            if (questionIds != null) {
                request.param("questionIds", questionIds);
            }
            if (topics != null) {
                request.param("topics", topics);
            }
            return mockMvc.perform(request);
        }
    }


    @Nested
    class Process {
        @Test
        void noAnswersGiven() throws Exception {
            GivenAnswers givenAnswer = new GivenAnswers(
                List.of("Q-20-04-18", "Q-20-04-13"),
                List.of()
            );
            request(givenAnswer)
                .andExpect(jsonPath("passed", is(true)))
                .andExpect(jsonPath("pointsMaximum", is(2.0)))
                .andExpect(jsonPath("totalPoints", is(0.0)))
                .andExpect(jsonPath("$..Q-20-04-13", hasItem(0.0)))
                .andExpect(jsonPath("$..Q-20-04-18", hasItem(0.0)));
        }

        @Test
        void allWrong() throws Exception {
            GivenAnswers givenAnswer = new GivenAnswers(
                List.of("Q-20-04-18", "Q-20-04-13"),
                List.of(
                    new AnsweredTask("Q-20-04-18", List.of('a')),
                    new AnsweredTask("Q-20-04-13", List.of('a'))
                )
            );
            request(givenAnswer)
                .andDo(print())
                .andExpect(jsonPath("passed", is(true)))
                .andExpect(jsonPath("pointsMaximum", is(2.0)))
                .andExpect(jsonPath("totalPoints", is(0.0)))
                .andExpect(jsonPath("$..Q-20-04-13", hasItem(0.0)))
                .andExpect(jsonPath("$..Q-20-04-18", hasItem(0.0)));
        }

        @Test
        void allCorrect() throws Exception {
            GivenAnswers givenAnswer = new GivenAnswers(
                List.of("Q-20-04-18", "Q-20-04-13"),
                List.of(
                    new AnsweredTask("Q-20-04-18", List.of('a')),
                    new AnsweredTask("Q-20-04-13", List.of('a'))
                )
            );
            request(givenAnswer)
//                .andDo(print())
                .andExpect(jsonPath("pointsMaximum", is(2.0)))
                .andExpect(jsonPath("passed", is(true)))
                .andExpect(jsonPath("totalPoints", is(2.0)))
                .andExpect(jsonPath("Q-20-04-13", is(1.0)))
                .andExpect(jsonPath("Q-20-04-18", is(1.0)));
        }

        private ResultActions request(GivenAnswers givenAnswer) throws Exception {
            String json = new ObjectMapper().writeValueAsString(givenAnswer);
            MockHttpServletRequestBuilder request = post("/api")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);
            return mockMvc.perform(request);
        }
    }


    @SuppressWarnings("unchecked")
    private List<String> filter(String json, String jsonPath) {
        var values = JsonPath.compile(jsonPath).read(json);
        return (List<String>) values;
    }
}
