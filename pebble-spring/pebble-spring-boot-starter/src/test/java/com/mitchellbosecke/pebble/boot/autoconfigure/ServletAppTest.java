package com.mitchellbosecke.pebble.boot.autoconfigure;

import com.mitchellbosecke.pebble.boot.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ServletAppTest {

  @Autowired
  private WebApplicationContext wac;

  protected MockMvc mockMvc;

  @Before
  public void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
  }

  @Test
  public void testOk() throws Exception {
    this.mockMvc.perform(get("/index.action")).andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
        .andExpect(content().string("Hello Pebbleworld!"));
  }

  @Test
  public void testRequestAccess() throws Exception {
    MvcResult result = this.mockMvc.perform(get("/contextPath.action")).andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)).andReturn();
    assertEquals("ctx path:" + result.getRequest().getContextPath(),
        result.getResponse().getContentAsString());
  }

  @Test
  public void testEnglishHello() throws Exception {
    this.mockMvc.perform(get("/hello.action").locale(Locale.forLanguageTag("en")))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
        .andExpect(content().string("Hello Boot!"));
  }

  @Test
  public void testSpanishHello() throws Exception {
    this.mockMvc.perform(get("/hello.action").locale(Locale.forLanguageTag("es")))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
        .andExpect(content().string("Hola Boot!"));
  }

  @Test
  public void testAdditionalExtensions() throws Exception {
    this.mockMvc.perform(get("/extensions.action").locale(Locale.forLanguageTag("es")))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
        .andExpect(content().string("Hola Boot! Tested!"));
  }

}
