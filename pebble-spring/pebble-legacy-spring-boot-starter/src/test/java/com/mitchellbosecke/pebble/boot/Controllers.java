package com.mitchellbosecke.pebble.boot;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class Controllers {

  @RequestMapping("/hello.action")
  public String hello() {
    return "hello";
  }

  @RequestMapping("/index.action")
  public String index() {
    return "index";
  }

  @RequestMapping("/contextPath.action")
  public String contextPath() {
    return "contextPath";
  }

  @RequestMapping("/extensions.action")
  public String extensions() {
    return "extensions";
  }

  @RequestMapping("/beans.action")
  public String beans() {
    return "beans";
  }

  @RequestMapping("/response.action")
  public String response() {
    return "responseObject";
  }

}