package org.isaqb.onlineexam.model;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

@Component
public class TaskValidator {

    public List<String> validate(Task task) {
        return Stream.of(
            assertFieldNotNull(task, Task::getId, "id"),
            assertFieldNotNull(task, Task::getType, "type"),
            assertTrue(task, "'reachablePoints' ungültig", t->t.getReachablePoints()>0),
            assertFieldNotNull(task, Task::getQuestion, "question"),
            assertTrue(task, "'question' ist leer", t->!t.getQuestion().isEmpty())
        )
            .filter(Optional::isPresent)
            .map(Optional<String>::get)
            .toList();
    }

    private Optional<String> assertTrue(Task task, String errorMessage, Predicate<Task> check) {
        return check.test(task) ? Optional.empty() : Optional.of(errorMessage);
    }

    private Optional<String> assertFieldNotNull(Task task, Function<Task,Object> fieldAccessor, String fieldName) {
        return fieldAccessor.apply(task) == null
                ? Optional.of(String.format("'%s' is null", fieldName))
                : Optional.empty();
    }

}
