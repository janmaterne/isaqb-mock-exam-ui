package org.isaqb.onlineexam.mockexam.loader;

import java.util.Optional;

public interface Loader {
    Optional<String> loadAsString(String remoteUrl);
}