package org.isaqb.onlineexam.mockexam;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Properties;

public class ApplicationProperties {

	Properties props;
	
	public ApplicationProperties() {
		props = new Properties();
		try {
			props.load(getClass().getResourceAsStream("/application.properties"));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	public String urlTemplate() {
		return props.getProperty("url.template");
	}
	
	public int urlFrom() {
		return Integer.parseInt(props.getProperty("url.from"));
	}
	
	public int urlTo() {
		return Integer.parseInt(props.getProperty("url.to"));
	}

	public int examRequiredPoints() {
		return Integer.parseInt(props.getProperty("exam.requiredPoints"));
	}

}
