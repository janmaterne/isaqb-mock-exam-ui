package org.isaqb.onlineexam.mockexam.loader;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Loads strings from remote urls. 
 * 
 * <p>The urls are calculated by a given {@linkplain #urlTemplate} 
 * The value of its placeholder <tt>{NR}</tt> is replaced by a loop
 * variable starting by {@linkplain #from} and increasing up to
 * {@linkplain #to} inclusive and formatted as two digits number.
 * The content of the remote resource is loaded as string.</p>   
 */
@Component
public class StringLoader {

	private String urlTemplate;
	private int from;
	private int to;
	
	public StringLoader(
		@Value("${url.template}") String urlTemplate, 
		@Value("${url.from}") int from, 
		@Value("${url.to}") int to
	) {
		this.urlTemplate = urlTemplate;
		this.from = from;
		this.to = to;
	}
	
	/**
	 * Load the content of a single remote url.
	 */
	public Optional<String> loadAsString(String remoteUrl) {
		try {
			return Optional.of(IOUtils.toString(new URL(remoteUrl), Charset.defaultCharset()));
		} catch (IOException e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}
	
	/**
	 * Generate the list of urls to load.
	 */
	public List<String> remoteUrls() {
		List<String> list = new ArrayList<>();
		for(int nr=from; nr<=to; nr++) {
			String nrAsString = (nr < 10) ? "0" + nr : String.valueOf(nr);
			String url = urlTemplate.replace("{NR}", nrAsString);
			list.add(url);
		}
		return list;
	}
	
}
