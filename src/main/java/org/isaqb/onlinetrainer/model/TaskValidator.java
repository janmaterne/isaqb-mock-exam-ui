package org.isaqb.onlinetrainer.model;

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
            assertFieldNotEmpty(task, Task::getId, "id"),
            assertFieldNotEmpty(task, Task::getType, "type"),
            assertTrue(task, "'reachablePoints' ungültig", t->t.getReachablePoints()>0),
            assertFieldNotEmpty(task, Task::getQuestion, "question"),
            assertTrue(task, "'question' ist leer", t->!t.getQuestion().isEmpty())
        )
            .filter(Optional::isPresent)
            .map(Optional<String>::get)
            .toList();
    }

    private Optional<String> assertTrue(Task task, String errorMessage, Predicate<Task> check) {
        return check.test(task) ? Optional.empty() : Optional.of(errorMessage);
    }

    private Optional<String> assertFieldNotEmpty(Task task, Function<Task,Object> fieldAccessor, String fieldName) {
        return isEmpty(fieldAccessor.apply(task))
                ? Optional.of(String.format("'%s' is null or empty", fieldName))
                : Optional.empty();
    }

    private boolean isEmpty(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof String) {
        	String asString = (String) value;
            if (asString.isBlank()) {
                return true;
            }
            if (asString.trim().equals("null")) {
            	return true;
            }
        }
        return false;
    }

}
