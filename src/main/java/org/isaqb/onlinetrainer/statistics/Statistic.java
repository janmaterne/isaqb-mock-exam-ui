package org.isaqb.onlinetrainer.statistics;

import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Statistic {
    @Id 
    private String key;
    private Integer value;
    public Statistic increase() {
        value++;
        return this;
    }
}
