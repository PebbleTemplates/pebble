package com.mitchellbosecke.pebble.utils;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Utility class to handle relative paths.
 *
 * @author Thomas Hunziker
 *
 */
public final class PathUtils {

    /**
     * Resolves the given {@code relativePath} based on the given
     * {@code anchorPath} by using the system default {@link File#separator}.
     *
     * @param relativePath
     *            the relative path which should be resolved.
     * @param anchorPath
     *            the anchor path based on which the relative path should be
     *            resolved on.
     * @return the resolved path or {@code null} when the path could not be resolved.
     */
    public static String resolveRelativePath(String relativePath, String anchorPath) {
        return resolveRelativePath(relativePath, anchorPath, File.separator);
    }

    /**
     * Resolves the given {@code relativePath} based on the given
     * {@code anchorPath}.
     *
     * @param relativePath
     *            the relative path which should be resolved.
     * @param anchorPath
     *            the anchor path based on which the relative path should be
     *            resolved on.
     * @param separator
     *            the path separator to use to resolve the path.
     * @return the resolved path or {@code null} when the path could not be resolved.
     */
    public static String resolveRelativePath(String relativePath, String anchorPath, String separator) {
        if (relativePath == null || relativePath.isEmpty()) {
            return null;
        }
        if (separator == null) {
            throw new IllegalArgumentException("The separator cannot be NULL.");
        }

        if (relativePath.startsWith(".." + separator) || relativePath.startsWith("." + separator)) {
            StringBuilder resultingPath = new StringBuilder();

            for (String segment : resolvePathSegments(determineAnchorPathSegments(anchorPath, separator),
                    Arrays.asList(relativePath.split(separator)))) {
                resultingPath.append(segment).append(separator);
            }
            return resultingPath.substring(0, resultingPath.length() - separator.length());
        }

        return null;
    }

    private static Collection<String> determineAnchorPathSegments(String anchorPath, String separator) {
        if (anchorPath == null || anchorPath.isEmpty()) {
            return new ArrayList<>();
        }
        ArrayDeque<String> anchorPathSegments = new ArrayDeque<>(Arrays.asList(anchorPath.split(separator)));
        if (!anchorPath.endsWith(separator)) {
            anchorPathSegments.pollLast();
        }
        return anchorPathSegments;
    }

    private static Collection<String> resolvePathSegments(Collection<String> anchorSegments,
            Collection<String> relativeSegements) {
        ArrayDeque<String> result = new ArrayDeque<>(anchorSegments);
        for (String segment : relativeSegements) {
            if (segment.equals(".")) {
                // do nothing
            } else if (segment.equals("..")) {
                result.pollLast();
            } else {
                result.add(segment);
            }
        }

        return result;
    }

    private PathUtils() {
        throw new IllegalAccessError();
    }

}
