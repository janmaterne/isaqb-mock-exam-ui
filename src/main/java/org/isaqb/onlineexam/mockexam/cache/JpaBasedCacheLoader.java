package org.isaqb.onlineexam.mockexam.cache;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.isaqb.onlineexam.mockexam.loader.Loader;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class JpaBasedCacheLoader implements Loader {

    private CacheRepository repository;
    private Loader delegate;
    private int ttlHours;



    @Override
    public Optional<String> loadAsString(String remoteUrl) {
        var opt = repository.findByUrl(remoteUrl);
        if (opt.isEmpty() || updateRequired(opt.get())) {
            updateCache(remoteUrl);
        }
        return repository.findByUrl(remoteUrl)
                .map(CacheEntry::getContent);
    }



    protected boolean updateRequired(CacheEntry cacheEntry) {
        return cacheEntry.getSaved() == null
            || cacheEntry.getSaved()
                .plus(ttlHours, ChronoUnit.HOURS)
                .isBefore(Instant.now());
    }

    protected void updateCache(String remoteUrl) {
        delegate.loadAsString(remoteUrl)
            .ifPresent( content -> repository.updateAndSave(new CacheEntry(remoteUrl, content)) );
    }

}
