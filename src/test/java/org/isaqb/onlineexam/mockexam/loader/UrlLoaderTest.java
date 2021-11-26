package org.isaqb.onlineexam.mockexam.loader;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class UrlLoaderTest {

    @Nested
    public class LoadAsString {
        @Test
        public void ok() {
            String remoteUrl = "https://raw.githubusercontent.com/isaqb-org/examination-foundation/master/raw/mock_exam/docs/questions/question-01.adoc";
            Optional<String> opt = new UrlLoader().loadAsString(remoteUrl);
            assertTrue(opt.isPresent());
            assertTrue(opt.get().contains("Q-20-04-01"));
        }

        @Test
        public void notLoaded() {
            String remoteUrl = "https://raw.githubusercontent.com/isaqb-org/examination-foundation/master/raw/mock_exam/docs/questions/question-0X1.adoc";
            Optional<String> opt = new UrlLoader().loadAsString(remoteUrl);
            assertFalse(opt.isPresent());
        }
    }

}
