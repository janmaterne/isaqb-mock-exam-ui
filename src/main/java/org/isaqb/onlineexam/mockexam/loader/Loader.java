package org.isaqb.onlineexam.mockexam.loader;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class Loader {

    @Cacheable
    public Optional<String> loadAsString(String remoteUrl) {
        System.out.printf("Load %s%n", remoteUrl);
        try {
            return Optional.of(IOUtils.toString(new URL(remoteUrl), StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.printf("%s: '%s'%n", e.getMessage(), remoteUrl);
            return Optional.empty();
        }
    }

}
