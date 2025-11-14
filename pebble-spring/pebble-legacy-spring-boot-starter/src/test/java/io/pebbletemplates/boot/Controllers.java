package io.pebbletemplates.boot;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.WebSession;

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

    @RequestMapping("/session.action")
    public String session(WebSession session) {
        session.getAttributes().put("foo", "bar");
        return "session";
    }

  @RequestMapping("/response.action")
  public String response() {
    return "responseObject";
  }

}