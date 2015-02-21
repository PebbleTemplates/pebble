package com.mitchellbosecke.pebble.template;

import java.lang.reflect.Member;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClassAttributeCache {

    private final Map<CacheKey, Member> attributes;

    public ClassAttributeCache() {
        this.attributes = new ConcurrentHashMap<CacheKey, Member>();
    }

    public boolean containsKey(Object object, String attributeName, Class<?>[] parameterTypes) {
        CacheKey key = new CacheKey(object, attributeName, parameterTypes);
        return attributes.containsKey(key);
    }

    public Member get(Object object, String attributeName, Class<?>[] parameterTypes) {
        CacheKey key = new CacheKey(object, attributeName, parameterTypes);
        return attributes.get(key);
    }

    public Member put(Object object, String attributeName, Class<?>[] parameterTypes, Member member) {
        CacheKey key = new CacheKey(object, attributeName, parameterTypes);
        return attributes.put(key, member);
    }

    private class CacheKey {

        private final Class<?> clazz;

        private final String attributeName;

        private final Class<?>[] parameterTypes;

        public CacheKey(Object object, String attributeName, Class<?>[] parameterTypes) {
            this.clazz = object.getClass();
            this.attributeName = attributeName;
            this.parameterTypes = parameterTypes;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = ((attributeName == null) ? 0 : attributeName.hashCode());
            result = prime * result + ((clazz == null) ? 0 : clazz.getName().hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            CacheKey other = (CacheKey) obj;
            if (attributeName == null) {
                if (other.attributeName != null)
                    return false;
            } else if (!attributeName.equals(other.attributeName))
                return false;
            if (clazz == null) {
                if (other.clazz != null)
                    return false;
            } else if (!clazz.equals(other.clazz))
                return false;
            if (!Arrays.equals(parameterTypes, other.parameterTypes))
                return false;
            return true;
        }

    }
}
