package org.isaqb.onlineexam.mockexam.cache;

import java.time.Instant;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@Entity
public class CacheEntry {
    @Id private String url;
    @Lob private String content;
    private Instant saved;

    public CacheEntry(String url, String content) {
        this.url = url;
        this.content = content;
    }

}
