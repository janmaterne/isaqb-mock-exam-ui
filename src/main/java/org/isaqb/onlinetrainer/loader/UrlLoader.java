package org.isaqb.onlinetrainer.loader;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.apache.commons.io.IOUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UrlLoader implements Loader {

    @Override
    public Optional<String> loadAsString(String remoteUrl) {
        log.debug("Load {}", remoteUrl);
        try {
            return Optional.of(IOUtils.toString(new URI(remoteUrl).toURL(), StandardCharsets.UTF_8));
        } catch (IOException | URISyntaxException e) {
            log.error("{}: remoteUrl='{}'", e.getMessage(), remoteUrl);
            return Optional.empty();
        }
    }

}
