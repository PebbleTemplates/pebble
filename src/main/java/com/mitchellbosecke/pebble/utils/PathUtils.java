package com.mitchellbosecke.pebble.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Utility class to handle relative paths.
 *
 * @author Thomas Hunziker
 *
 */
public final class PathUtils {

    private static final String UNIX_SEPARATOR = "/";

    private static final String WINDOWS_SEPARATOR = "\\";

    /**
     * Resolves the given {@code relativePath} based on the given
     * {@code anchorPath}.
     *
     * @param relativePath
     *            the relative path which should be resolved.
     * @param anchorPath
     *            the anchor path based on which the relative path should be
     *            resolved on.
     * @param anchorPathSeparator
     *            the path separator to use to resolve the path.
     * @return the resolved path or {@code null} when the path could not be
     *         resolved.
     */
    public static String resolveRelativePath(String relativePath, String anchorPath) {
        if (relativePath == null || relativePath.isEmpty()) {
            return null;
        }

        // The assumption is that when a relative path starts with an operating
        // system specific separator we can treat the rest of the path also the
        // same way.
        if (relativePath.startsWith(".." + UNIX_SEPARATOR) || relativePath.startsWith("." + UNIX_SEPARATOR)) {
            return resolvePathInner(relativePath, anchorPath, UNIX_SEPARATOR);
        } else if (relativePath.startsWith(".." + WINDOWS_SEPARATOR)
                || relativePath.startsWith("." + WINDOWS_SEPARATOR)) {
            return resolvePathInner(relativePath, anchorPath, WINDOWS_SEPARATOR);
        }

        return null;
    }

    private static String resolvePathInner(String relativePath, String anchorPath, String separator) {
        StringBuilder resultingPath = new StringBuilder();

        for (String segment : resolvePathSegments(determineAnchorPathSegments(anchorPath, separator),
                splitBySeparator(relativePath, separator))) {
            resultingPath.append(segment).append(separator);
        }
        return resultingPath.substring(0, resultingPath.length() - separator.length());
    }

    private static Collection<String> determineAnchorPathSegments(String anchorPath, String separator) {
        if (anchorPath == null || anchorPath.isEmpty()) {
            return new ArrayList<>();
        }
        ArrayDeque<String> anchorPathSegments = new ArrayDeque<>(splitBySeparator(anchorPath, separator));
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

    private static List<String> splitBySeparator(String path, String separator) {
        return Arrays.asList(path.split(separator.replace("\\", "\\\\")));
    }

    private PathUtils() {
        throw new IllegalAccessError();
    }

}
