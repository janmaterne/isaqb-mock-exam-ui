package org.isaqb.onlinetrainer.loader;

import java.util.Optional;

public interface Loader {
    Optional<String> loadAsString(String remoteUrl);
}