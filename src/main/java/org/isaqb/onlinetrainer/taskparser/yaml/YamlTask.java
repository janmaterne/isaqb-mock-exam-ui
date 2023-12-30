package org.isaqb.onlinetrainer.taskparser.yaml;

import java.util.List;
import java.util.Map;

import org.isaqb.onlinetrainer.model.Language;

import lombok.Data;

/**
 * Data-Structure for the YAML-Format.
 */
@Data
public class YamlTask {

    private String id;
    private String type;
    private int reachablePoints;
    private Map<Language,String> question;
    private String explanation;
    private Map<Language, List<String>> columnHeaders;
    private List<PossibleOption> possibleOptions;

    @Data
    public static class PossibleOption {
        private String position;
        private Map<Language,String> text;
        private String columnValues;
    }

}
