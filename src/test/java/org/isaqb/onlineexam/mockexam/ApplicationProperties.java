package org.isaqb.onlineexam.mockexam;

public class ApplicationProperties {

	public String urlTemplate() {
		return "https://raw.githubusercontent.com/isaqb-org/examination-foundation/main/raw/mock_exam/docs/questions/question-{NR}.adoc";
	}
	
	public int urlFrom() {
		return 1;
	}
	
	public int urlTo() {
		return 5;
	}

	public int examRequiredPoints() {
		return 30;
	}

}
