package org.isaqb.onlineexam.util;

import java.util.Collection;
import java.util.List;

import org.isaqb.onlineexam.calculation.CalculationResult;
import org.isaqb.onlineexam.model.TaskAnswer;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AllArgsConstructor;


@Component
@AllArgsConstructor
public class JsonMapper {

	private ObjectMapper mapper;
	private Base64Handler base64;

	public String toString(Collection<TaskAnswer> answers) {
		return mapToString(answers);
	}

	public String toString(CalculationResult result) {
		return mapToString(result);
	}

	private String mapToString(Object value) {
		try {
			String json = mapper.writer().writeValueAsString(value);
			return base64.encode(json);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}



	public List<TaskAnswer> fromStringToAnswers(String jsonBase64) {
		try {
			String json = base64.decode(jsonBase64);
			return mapper.readValue(json, new TypeReference<List<TaskAnswer>>() {} );
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	public CalculationResult fromStringToCalculationResult(String jsonBase64) {
		try {
			String json = base64.decode(jsonBase64);
			return mapper.readValue(json, CalculationResult.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

}
