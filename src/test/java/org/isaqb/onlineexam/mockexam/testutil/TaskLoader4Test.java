package org.isaqb.onlineexam.mockexam.testutil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.isaqb.onlineexam.mockexam.model.Task;
import org.isaqb.onlineexam.mockexam.parser.TaskParser;

public class TaskLoader4Test {
	
	public static Path TEST_RESOURCES_DIR = Paths.get("src", "test", "resources");
	
	public static TaskLoader4Test create(String... subdirs) {
		Path resDir = TEST_RESOURCES_DIR;
		for(String sub : subdirs) {
			resDir = resDir.resolve(sub);
		}
		return new TaskLoader4Test(resDir);
	}

	private Path resourceDir;

	private TaskLoader4Test(Path resourceDir) {
		this.resourceDir = resourceDir;
	}
	
	public Task loadTask(String resourceName) throws IOException {
		String adocContent = readResource(resourceName);
		return new TaskParser().parseADoc(adocContent);
	}
	
	private String readResource(String name) throws IOException {
		return Files.readString(resourceDir.resolve(name));
	}

}
