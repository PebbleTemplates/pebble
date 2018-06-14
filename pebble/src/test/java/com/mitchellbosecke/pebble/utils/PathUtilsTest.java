package com.mitchellbosecke.pebble.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests {@link PathUtils}.
 *
 * @author Thomas Hunziker
 */
public class PathUtilsTest {

  private static final char FORWARD_SLASH = '/';
  private static final char BACKWARD_SLASH = '\\';

  /**
   * Tests if {@link PathUtils#resolveRelativePath(String, String, char)} is working with Unix
   * systems.
   */
  @Test
  public void testRelativePathResolutionUnixStyle() {
    Assert.assertEquals("test/test/sample.peb",
        PathUtils.resolveRelativePath("./sample.peb", "test/test/", FORWARD_SLASH));
    Assert.assertEquals("test/test/sample.peb",
        PathUtils.resolveRelativePath("./sample.peb", "test/test/other.peb", FORWARD_SLASH));
    Assert.assertEquals("test/sample.peb",
        PathUtils.resolveRelativePath("../sample.peb", "test/test/", FORWARD_SLASH));
    Assert.assertEquals("test/sample.peb",
        PathUtils.resolveRelativePath("../sample.peb", "test/test/other.peb", FORWARD_SLASH));
    Assert.assertNull(
        PathUtils.resolveRelativePath("test/sample.peb", "test/test/other.peb", FORWARD_SLASH));

  }

  /**
   * Tests if {@link PathUtils#resolveRelativePath(String, String, char)} is working with Windows.
   */
  @Test
  public void testRelativePathResolutionWindowsStyle() {
    Assert.assertEquals("test\\test\\sample.peb",
        PathUtils.resolveRelativePath(".\\sample.peb", "test\\test\\", BACKWARD_SLASH));
    Assert.assertEquals("test\\test\\sample.peb",
        PathUtils.resolveRelativePath(".\\sample.peb", "test\\test\\other.peb", BACKWARD_SLASH));
    Assert.assertEquals("test\\sample.peb",
        PathUtils.resolveRelativePath("..\\sample.peb", "test\\test\\", BACKWARD_SLASH));
    Assert.assertEquals("test\\sample.peb",
        PathUtils.resolveRelativePath("..\\sample.peb", "test\\test\\other.peb", BACKWARD_SLASH));
    Assert.assertEquals(null,
        PathUtils.resolveRelativePath("test\\sample.peb", "test\\test\\other.peb", BACKWARD_SLASH));

  }


  @Test
  public void testRelativePathResolutionMixedStyle1() {
    Assert.assertEquals("test/test/sample.peb",
        PathUtils.resolveRelativePath(".\\sample.peb", "test/test/", FORWARD_SLASH));
    Assert.assertEquals("test/test/sample.peb",
        PathUtils.resolveRelativePath(".\\sample.peb", "test/test/other.peb", FORWARD_SLASH));
    Assert.assertEquals("test/sample.peb",
        PathUtils.resolveRelativePath("..\\sample.peb", "test/test/", FORWARD_SLASH));
    Assert.assertEquals("test/sample.peb",
        PathUtils.resolveRelativePath("..\\sample.peb", "test/test/other.peb", FORWARD_SLASH));
    Assert.assertEquals(null,
        PathUtils.resolveRelativePath("test\\sample.peb", "test/test/other.peb", FORWARD_SLASH));
  }

  @Test
  public void testRelativePathResolutionMixedStyle2() {
    Assert.assertEquals("test\\test\\sample.peb",
        PathUtils.resolveRelativePath("./sample.peb", "test\\test\\", BACKWARD_SLASH));
    Assert.assertEquals("test\\test\\sample.peb",
        PathUtils.resolveRelativePath("./sample.peb", "test\\test\\other.peb", BACKWARD_SLASH));
    Assert.assertEquals("test\\sample.peb",
        PathUtils.resolveRelativePath("../sample.peb", "test\\test\\", BACKWARD_SLASH));
    Assert.assertEquals("test\\sample.peb",
        PathUtils.resolveRelativePath("../sample.peb", "test\\test\\other.peb", BACKWARD_SLASH));
    Assert.assertEquals(null,
        PathUtils.resolveRelativePath("test/sample.peb", "test\\test\\other.peb", BACKWARD_SLASH));
  }
}
