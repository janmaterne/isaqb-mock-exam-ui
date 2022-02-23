package org.isaqb.onlinetrainer.cache;

import java.time.Instant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CacheRepository extends JpaRepository<CacheEntry, String> {

    Optional<CacheEntry> findByUrl(String url);

    default void updateAndSave(CacheEntry cacheEntry) {
        cacheEntry.setSaved(Instant.now());
        save(cacheEntry);
    }

}
