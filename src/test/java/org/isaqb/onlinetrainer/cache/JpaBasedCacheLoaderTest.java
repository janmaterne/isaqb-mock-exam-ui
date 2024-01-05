package org.isaqb.onlinetrainer.cache;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import org.isaqb.onlinetrainer.loader.Loader;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class JpaBasedCacheLoaderTest {

    @Mock CacheRepository repository;
    @Mock Loader delegate;



    private JpaBasedCacheLoader cache() {
        return new JpaBasedCacheLoader(repository, delegate, 25);
    }


    @Nested
    class UpdateRequired {

        @Test
        void noTimestamp() {
            CacheEntry entry = new CacheEntry();
            assertTrue(cache().updateRequired(entry));
        }

        @Test
        void tooOld() {
            CacheEntry entry = new CacheEntry().setSaved(Instant.now().minus(10, ChronoUnit.DAYS));
            assertTrue(cache().updateRequired(entry));
        }

        @Test
        void actual() {
            CacheEntry entry = new CacheEntry().setSaved(Instant.now());
            assertFalse(cache().updateRequired(entry));
        }
    }


    @Nested
    class UpdateCache {

        @Test
        void noContentLoaded() {
            when(delegate.loadAsString(anyString()))
                .thenReturn(Optional.empty());
            cache().updateCache("url");
            verify(repository, times(0))
                .updateAndSave(any());
        }

        @Test
        void contentLoaded() {
            when(delegate.loadAsString(anyString()))
                .thenReturn(Optional.of("content"));
            cache().updateCache("url");
            verify(repository)
                .updateAndSave(any());
        }

        @Test
        void timestampSet() {
            when(delegate.loadAsString(anyString()))
                .thenReturn(Optional.of("content"));
            repository = mock(MockingRepository.class);
            doCallRealMethod().when(repository).updateAndSave(any());
            doCallRealMethod().when(repository).save(any());

            cache().updateCache("url");

            var entry = ((MockingRepository)repository).value;
            assertNotNull(entry.getSaved());
        }

        abstract class MockingRepository implements CacheRepository {
            CacheEntry value;
            @Override
            public <S extends CacheEntry> S save(S entity) {
                value = entity;
                return null;
            }
        }
    }


    @Nested
    class LoadAsString {

        @Test
        void noCachedValue() {
            when(repository.findByUrl(anyString()))
                .thenReturn(Optional.empty());
            cache().loadAsString("url");
            verify(delegate).loadAsString(anyString());
        }

        @Test
        void cachedValueWithoutTimestamp() {
            when(repository.findByUrl(anyString()))
                .thenReturn(Optional.of(new CacheEntry()));
            cache().loadAsString("url");
            verify(delegate).loadAsString(anyString());
        }

        @Test
        void cachedValueTooOld() {
            when(repository.findByUrl(anyString()))
                .thenReturn(Optional.of(new CacheEntry().setSaved(Instant.now().minus(10, ChronoUnit.DAYS))));
            cache().loadAsString("url");
            verify(delegate).loadAsString(anyString());
        }

        @Test
        void cachedValueActual() {
            when(repository.findByUrl(anyString()))
                .thenReturn(Optional.of(new CacheEntry().setSaved(Instant.now())));
            cache().loadAsString("url");
            verify(delegate, times(0)).loadAsString(anyString());
        }
    }

}
