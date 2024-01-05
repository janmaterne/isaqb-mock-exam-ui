package org.isaqb.onlinetrainer.statistics;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Statistic {

    @Id 
    @Column(name="stat-key")
    private String key;

    @Column(name="stat-value")
    private Integer value;

    public Statistic increase() {
        value++;
        return this;
    }

}
