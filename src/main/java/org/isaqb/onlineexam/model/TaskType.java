package org.isaqb.onlineexam.model;

public enum TaskType {
	
	
	SINGLE_CHOICE('A'),
	PICK_FROM_MANY('P'),
	CHOOSE('K');
	
	public char type;

	private TaskType(char type) {
		this.type = type;
	}

	public static TaskType of(String value) {
		char kind = value.trim().toUpperCase().charAt(0);
		if (kind == 'A') return SINGLE_CHOICE;
		if (kind == 'P') return PICK_FROM_MANY;
		if (kind == 'K') return CHOOSE;
		return null;
	}
}
