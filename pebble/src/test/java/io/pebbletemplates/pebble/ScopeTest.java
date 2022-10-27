package io.pebbletemplates.pebble;

import io.pebbletemplates.pebble.template.Scope;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ScopeTest {

    @Test
    void testGetKeys() {
        Map<String, Object> map = new HashMap<>();
        map.putIfAbsent("key1", new String("value1"));
        map.putIfAbsent("key2", new String("value2"));

        Scope scope = new Scope(map, false);
        Set<String> expected = new HashSet<>();
        expected.add("key1");
        expected.add("key2");

        assertEquals(expected, scope.getKeys());
    }
}
