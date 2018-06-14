/**
 *
 */
package com.mitchellbosecke.pebble.spring4.util;

import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class ViewUtils {

  private ViewUtils() {
  }

  public static HttpServletRequest getRequest() {
    ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder
        .currentRequestAttributes();
    return attr.getRequest();
  }
}
