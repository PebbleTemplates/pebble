package com.mitchellbosecke.pebble.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests {@link PathUtils}.
 *
 * @author Thomas Hunziker
 *
 */
public class PathUtilsTest {

    /**
     * Tests if {@link PathUtils#resolveRelativePath(String, String, String)} is working.
     */
    @Test
    public void testRelativePathResolution() {
        Assert.assertEquals("test/test/sample.peb",
                PathUtils.resolveRelativePath("./sample.peb", "test/test/", "/"));
        Assert.assertEquals("test/test/sample.peb",
                PathUtils.resolveRelativePath("./sample.peb", "test/test/other.peb", "/"));
        Assert.assertEquals("test/sample.peb",
                PathUtils.resolveRelativePath("../sample.peb", "test/test/", "/"));
        Assert.assertEquals("test/sample.peb",
                PathUtils.resolveRelativePath("../sample.peb", "test/test/other.peb", "/"));
        Assert.assertEquals("test/sample.peb",
                PathUtils.resolveRelativePath("test/sample.peb", "test/test/other.peb", "/"));

    }

}
