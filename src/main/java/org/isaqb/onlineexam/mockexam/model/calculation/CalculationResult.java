package org.isaqb.onlineexam.mockexam.model.calculation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class CalculationResult {
	
	public Map<String, Double> points;
	public double pointsMaximum;
	public boolean passed;
	List<String> wrongAnswers = new ArrayList<>();
	
	public double pointsRelative() {
		return roundCent(totalPoints() * 100 / pointsMaximum);
	}
	
	private double roundCent(double value) {
		int i = (int) (100 * value);
		return 1.0 * i / 100;
	}
	
	public double totalPoints() {
		return points.values().stream().mapToDouble(Double::valueOf).sum();
	}
	
}