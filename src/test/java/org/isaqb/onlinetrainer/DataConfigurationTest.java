package org.isaqb.onlinetrainer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.isaqb.onlinetrainer.DataConfiguration.UrlTemplateConfig;
import org.isaqb.onlinetrainer.model.Language;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(DataConfiguration.class)
public class DataConfigurationTest {

    @Autowired
    private DataConfiguration config;

    @Test
    public void yamlInjected() {
        assertNotNull(config);
    }

    @Test
    public void simpleTasks() {
        var ddd = config.getTasks().get("ddd");
        assertEquals("Domain Driven Design", ddd.getName().get(Language.DE));
        assertEquals(1, ddd.getUrls().size());
        assertTrue(ddd.getUrls().get(0).getUrlTemplate().contains("questions/ddd"));
        assertEquals(1, ddd.getUrls().get(0).getFrom());
        assertEquals(5, ddd.getUrls().get(0).getTo());
        assertEquals(5, ddd.getUrls().get(0).generateUrls().size());
    }

    @Test
    public void resolveTaskRefs() {
        var conf = config.getTasks().get("foundation");
        assertTrue(conf.getRefs().isEmpty(), () -> conf.toString());
        assertEquals(4, conf.getUrls().size(), () -> conf.toString());
    }

    @Test
    public void examConfig() {
        var conf = config.getExams().get("mock");
        assertEquals("Foundation Level Mock Prüfung", conf.getName().get(Language.DE));
        assertEquals(30, conf.getRequiredPoints());
        assertEquals(60, conf.getMaxTimeInMinutes());
        assertEquals("foundation", conf.getTaskRefs().get(0));
    }
    
    @Test
    void skipTask() {
    	var ddd = config.getTasks().get("ddd");
    	ddd.setSkip(false);
    	assertTrue(ddd.generateUrls().count() > 0);
    	ddd.setSkip(true);
    	assertFalse(ddd.generateUrls().count() > 0);
    }



    @Nested
    public class DataConfiguration_UrlTemplateConfig {

        @Test
        public void remoteUrls() {
            UrlTemplateConfig cfg = new UrlTemplateConfig()
                .setUrlTemplate("http://question-{NR}.adoc")
                .setFrom(9)
                .setTo(11);
            var remoteUrls = cfg.generateUrls();
            assertEquals(3, remoteUrls.size());
            assertTrue(remoteUrls.contains("http://question-09.adoc"));
            assertTrue(remoteUrls.contains("http://question-10.adoc"));
            assertTrue(remoteUrls.contains("http://question-11.adoc"));
        }
    }
}
