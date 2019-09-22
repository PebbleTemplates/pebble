package com.mitchellbosecke.pebble.template;

/**
 * A scope is a map of variables. A "local" scope ensures that the search for a particular variable
 * will end at this scope whether or not it was found.
 *
 * @author Mitchell
 */
public interface Scope {
    /**
     * Creates a shallow copy of the Scope.
     * <p>
     * This is used for the parallel tag  because every new thread should have a "snapshot" of the
     * scopes, i.e. one thread should not affect rendering output of another.
     * <p>
     * It will construct a new collection but it will contain references to all of the original
     * variables therefore it is not a deep copy. This is why it is import for the user to use
     * thread-safe variables when using the parallel tag.
     *
     * @return A copy of the scope
     */
    Scope shallowCopy();

    /**
     * Adds a variable to this scope
     *
     * @param key The name of the variable
     * @param value The value of the variable
     */
    void put(String key, Object value);

    /**
     * Retrieves the variable at this scope
     *
     * @param key The name of the variable
     * @return The value of the variable
     */
    Object get(String key);

    /**
     * Checks if this scope contains a variable of a certain name.
     *
     * @param key The name of the variable
     * @return boolean stating whether or not the backing map of this scope contains that variable
     */
    boolean containsKey(String key);

    /**
     * Returns whether or not this scope is "local".
     *
     * @return boolean stating whether this scope is local or not.
     */
    boolean isLocal();

    /**
     * Indicate that this scope supports put operation
     * @return true if modifications are supported
     */
    boolean isWritable();
}
