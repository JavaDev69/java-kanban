package ru.yandex.practicum.vilkovam.manager;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

/**
 * @author Andrew Vilkov
 * @created 20.09.2025 - 17:13
 * @project java-kanban
 */
class IdGeneratorImplTest {

    @Test
    void nextIdShouldReturnIncrementingIds() {
        IdGenerator idGenerator = new IdGeneratorImpl();
        List<Integer> expected = Stream.iterate(1, i -> i + 1).limit(100).toList();

        List<Integer> actual = Stream.generate(idGenerator::nextId).limit(100).toList();
        assertIterableEquals(expected, actual, "Последовательность генератора не соответсвует");
    }
}