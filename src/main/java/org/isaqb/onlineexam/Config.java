package org.isaqb.onlineexam;

import org.isaqb.onlineexam.cache.CacheRepository;
import org.isaqb.onlineexam.cache.JpaBasedCacheLoader;
import org.isaqb.onlineexam.loader.Loader;
import org.isaqb.onlineexam.loader.UrlLoader;
import org.isaqb.onlineexam.ui.AutoloadJS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {

    @Bean
    public Loader publishLoader(CacheRepository repo, @Value("${cache.ttl-hours}") int ttlHours) {
        var realLoader = new UrlLoader();
        var cache = new JpaBasedCacheLoader(repo, realLoader, ttlHours);  //new CachingLoader(realLoader);
        return cache;
    }

    @Bean
    public static AutoloadJS create(@Value("${inject-autoload-js}") boolean injectAutoloadJS) {
        return springDevToolsPresent() && injectAutoloadJS
            ? new AutoloadJS(
                "<!-- Autoreload on local file change -->" +
                "<script type=\"text/javascript\" src=\"https://livejs.com/live.js\"></script>")
            : new AutoloadJS("");
	}

    private static boolean springDevToolsPresent() {
        try {
            Class.forName("org.springframework.boot.devtools.RemoteSpringApplication");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
