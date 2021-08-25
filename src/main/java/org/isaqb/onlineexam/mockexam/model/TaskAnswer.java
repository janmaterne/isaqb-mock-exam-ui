package org.isaqb.onlineexam.mockexam.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@NoArgsConstructor
@Data
@Accessors(chain = true)
public class TaskAnswer {
	private int taskNumber;
	private boolean flagged;
	private Map<String, List<String>> optionSelections = new HashMap<>();

	public TaskAnswer(int nr) {
		this.taskNumber = nr;
	}
	
	public TaskAnswer put(String optionPosition, String value) {
		optionSelections.computeIfAbsent(optionPosition, (ch) -> new ArrayList<>()).add(value);
		return this;
	}

	public TaskAnswer put(String optionPosition, List<String> value) {
		optionSelections.computeIfAbsent(optionPosition, (ch) -> new ArrayList<>()).addAll(value);
		return this;
	}
}
