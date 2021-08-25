package org.isaqb.onlineexam.mockexam.model;

import static org.isaqb.onlineexam.mockexam.util.ArrayUtil.asList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Option {
	
	/** Position of this option in the list like 'a', 'b', 'c' ... */
	private char position;
	
	/**
	 * An option could have multiple columns to check (true|false, part-of-view1|part-of-view2|...).
	 * So we hold for each of these columns their values defined in the asciidoc.
	 */
	private List<Character> columnValues = new ArrayList<>();
	
	/** The internationalized Text of this option. */
	private I18NText text = new I18NText();
	
	

	public Option(char position, boolean correct, Map<Language, String> text) {
		this(position, correct, new I18NText(text));
	}
	
	public Option(char position, boolean correct, I18NText text) {
		this.position = position;
		this.text = text;
		setCorrect(correct);
	}
	
	
	/**
	 * Find options in a list of options according to their positions.
	 * @param answers list of possible options
	 * @param positions list (vararg) of positions ('a', 'b'...) to search for
	 * @return a list (not null, but maybe empty) with foundings
	 * @see Task#getPossibleOptions() retrieving a candidate for this method
	 */
	public static List<Option> findByPosition(List<Option> answers, char... positions) {
		List<Character> posList = asList(positions);
		return answers.stream()
			.filter( a -> posList.contains(Character.valueOf(a.getPosition())) )
			.toList();
	}
	
	
	
	public String getText(Language language) {
		return text.getText(language);
	}
	
	public void putText(Language language, String text) {
		this.text.addText(language, text);
	}


	
	public void addColumnValue(char value) {
		columnValues.add(value);
	}
	
	/**
	 * Get all column indices (starting with 0) which are marked as 'correct'.
	 */
	public List<Integer> getCorrectColumnsIndices() {
		List<Integer> list = new ArrayList<>();
		for (int i = 0; i < columnValues.size(); i++) {
			char columnValue = columnValues.get(i);
			if (columnValue=='y' || columnValue=='Y') {
				list.add(i);
			}
		}
		return list;
	}
	
	/**
	 * Gets the first column index (starting with 0) which is marked as 'correct'.
	 */
	public Optional<Integer> getFirstCorrectColumnIndex() {
		return getCorrectColumnsIndices().stream().findFirst();
	}
	
	/**
	 * Try to behave like a boolean as this is the usecase for 2 of 3 task types - here the getter.
	 */
	public boolean isCorrect() {
		return getFirstCorrectColumnIndex().isPresent();
	}
	
	public boolean isCorrect(int index) {
		return index < columnValues.size()
			&& (columnValues.get(index) == 'y' || columnValues.get(index) == 'Y');
	}
	
	/**
	 * Try to behave like a boolean as this is the usecase for 2 of 3 task types - here the setter.
	 */
	public Option setCorrect(boolean correct) {
		char value = correct ? 'y' : 'n';
		columnValues.clear();
		columnValues.add(value);
		return this;
	}

}
 