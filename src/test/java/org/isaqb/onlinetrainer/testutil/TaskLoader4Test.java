package org.isaqb.onlinetrainer.testutil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.isaqb.onlinetrainer.model.Task;
import org.isaqb.onlinetrainer.taskparser.TaskParserChain;
import org.isaqb.onlinetrainer.taskparser.asciidoc.AsciidocTaskParser;
import org.isaqb.onlinetrainer.taskparser.simple.SimpleTaskParser;
import org.isaqb.onlinetrainer.taskparser.yaml.Yaml2ModelMapper;
import org.isaqb.onlinetrainer.taskparser.yaml.YamlTaskParser;
import org.mapstruct.factory.Mappers;

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

	public Task loadADocTask(String resourceName) throws IOException {
		String adocContent = readResource(resourceName);
		return new AsciidocTaskParser().parseContent(adocContent);
	}

    public Task loadYamlTask(String resourceName) throws IOException {
        String yamlContent = readResource(resourceName);
        return yamlParser().parseContent(yamlContent);
    }

	public Task loadSimpleTask(String resourceName) throws IOException {
        String content = readResource(resourceName);
		return new SimpleTaskParser().parseContent(content);
	}

    public Task loadTask(String resourceName) throws IOException {
        String adocContent = readResource(resourceName);
        return new TaskParserChain(new AsciidocTaskParser(), yamlParser()).parseContent(adocContent);
    }

	private String readResource(String name) throws IOException {
		return Files.readString(resourceDir.resolve(name));
	}

    private YamlTaskParser yamlParser() {
        return new YamlTaskParser(Mappers.getMapper(Yaml2ModelMapper.class));
    }

}
