package com.mitchellbosecke.pebble.utils;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class PublicMapEntry<K, V> implements Map.Entry<K, V> {

    private K key;
    private V value;

    private PublicMapEntry(Map.Entry<K, V> source) {
        this.key = source.getKey();
        this.value = source.getValue();
    }

    public static <K, V> Set<Map.Entry<K, V>> fromEntrySet(Set<Map.Entry<K, V>> source) {
        Set<Map.Entry<K, V>> ret = new LinkedHashSet<>();
        for (Map.Entry sourceMe : source) {
            ret.add(new PublicMapEntry(sourceMe));
        }
        return ret;
    }

    @Override
    public K getKey() {
        return this.key;
    }

    @Override
    public V getValue() {
        return this.value;
    }

    @Override
    public V setValue(V value) {
        V aux = this.value;
        this.value = value;
        return aux;
    }
}