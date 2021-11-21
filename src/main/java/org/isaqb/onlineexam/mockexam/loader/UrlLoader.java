package org.isaqb.onlineexam.mockexam.loader;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.apache.commons.io.IOUtils;

public class UrlLoader implements Loader {

    @Override
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
