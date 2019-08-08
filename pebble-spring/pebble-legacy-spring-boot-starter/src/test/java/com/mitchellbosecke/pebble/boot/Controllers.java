package com.mitchellbosecke.pebble.boot;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Controllers {

  @RequestMapping("/hello.action")
  public String hello() {
    return "hello";
  }

  @RequestMapping("/index.action")
  public String index(ModelMap model) {
    return "index";
  }

  @RequestMapping("/contextPath.action")
  public String contextPath(ModelMap model) {
    return "contextPath";
  }

  @RequestMapping("/extensions.action")
  public String extensions(ModelMap model) {
    return "extensions";
  }

}