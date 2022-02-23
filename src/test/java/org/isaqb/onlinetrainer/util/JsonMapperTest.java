package org.isaqb.onlinetrainer.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.isaqb.onlinetrainer.calculation.CalculationResult;
import org.isaqb.onlinetrainer.model.TaskAnswer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JsonMapperTest {

    @Autowired
    private JsonMapper mapper;

    @Test
    public void answer() {
        TaskAnswer original = new TaskAnswer();
        original.setFlagged(true).setTaskNumber(42);
        original.setOptionSelections(Map.of(
            "1", Arrays.asList("one", "eins"),
            "2", Arrays.asList("two", "zwei")
        ));
        List<TaskAnswer> list = Arrays.asList(original);
        assertEquals(list, mapper.fromStringToAnswers(mapper.toString(list)));
    }

    @Test
    public void calculationResult() {
        CalculationResult calcResult = new CalculationResult();
        calcResult.points = Map.of(
            "task-id-1", 3.2,
            "task-id-2", 4.4
        );
        calcResult.passed = true;
        calcResult.pointsMaximum = 7;

        CalculationResult turnaround = mapper.fromStringToCalculationResult(mapper.toString(calcResult));
        assertEquals(calcResult, turnaround);
    }

}
