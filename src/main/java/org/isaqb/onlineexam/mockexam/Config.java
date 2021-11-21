package org.isaqb.onlineexam.mockexam;

import org.isaqb.onlineexam.mockexam.cache.CacheRepository;
import org.isaqb.onlineexam.mockexam.cache.JpaBasedCacheLoader;
import org.isaqb.onlineexam.mockexam.loader.Loader;
import org.isaqb.onlineexam.mockexam.loader.UrlLoader;
import org.isaqb.onlineexam.mockexam.ui.AutloadJS;
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
    public static AutloadJS create() {
        return springDevToolsPresent()
            ? new AutloadJS(
                "<!-- Autoreload on local file change -->" +
                "<script type=\"text/javascript\" src=\"https://livejs.com/live.js\"></script>")
            : new AutloadJS("");
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
