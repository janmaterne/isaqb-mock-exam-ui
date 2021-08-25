package org.isaqb.onlineexam.mockexam.util;

import java.util.Base64;
import java.util.Collection;
import java.util.List;

import org.isaqb.onlineexam.mockexam.model.TaskAnswer;
import org.isaqb.onlineexam.mockexam.model.calculation.CalculationResult;
import org.isaqb.onlineexam.mockexam.ui.UIData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


@Component
public class JsonMapper {

	@Autowired
	private ObjectMapper mapper;
	
	public String toString(Collection<TaskAnswer> answers) {
		return mapToString(answers);
	}
	
	public String toString(CalculationResult result) {
		return mapToString(result);
	}
	
	private String mapToString(Object value) {
		try {
			String json = mapper.writer().writeValueAsString(value);
			if (value instanceof UIData) {
				System.out.printf("%n%ntoString()%n%s%n%n%n", json);
			}
			return encode(json);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	
	
	public List<TaskAnswer> fromStringToAnswers(String jsonBase64) {
		try {
			String json = decode(jsonBase64);
			return mapper.readValue(json, new TypeReference<List<TaskAnswer>>() {} );
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public CalculationResult fromStringToCalculationResult(String jsonBase64) {
		try {
			String json = decode(jsonBase64);
			return mapper.readValue(json, CalculationResult.class);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
	protected String encode(String json) {
		return Base64.getEncoder().encodeToString(json.getBytes());
	}

	protected String decode(String jsonBase64) {
		return new String(Base64.getDecoder().decode(jsonBase64));
	}
	
}
