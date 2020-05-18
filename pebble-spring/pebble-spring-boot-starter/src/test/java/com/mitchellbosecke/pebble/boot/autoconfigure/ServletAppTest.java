package com.mitchellbosecke.pebble.boot.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mitchellbosecke.pebble.boot.Application;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(classes = Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
class ServletAppTest {

  @Autowired
  private WebApplicationContext wac;

  protected MockMvc mockMvc;

  @BeforeEach
  void setup() {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
  }

  @Test
  void testOk() throws Exception {
    this.mockMvc.perform(get("/index.action")).andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
        .andExpect(content().string("Hello Pebbleworld!"));
  }

  @Test
  void testRequestAccess() throws Exception {
    MvcResult result = this.mockMvc.perform(get("/contextPath.action")).andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML)).andReturn();

    assertThat(result.getResponse().getContentAsString()).isEqualTo("ctx path:" + result.getRequest().getContextPath());
  }

  @Test
  void testEnglishHello() throws Exception {
    this.mockMvc.perform(get("/hello.action").locale(Locale.forLanguageTag("en")))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
        .andExpect(content().string("Hello Boot!"));
  }

  @Test
  void testSpanishHello() throws Exception {
    this.mockMvc.perform(get("/hello.action").locale(Locale.forLanguageTag("es")))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
        .andExpect(content().string("Hola Boot!"));
  }

  @Test
  void testAdditionalExtensions() throws Exception {
    this.mockMvc.perform(get("/extensions.action").locale(Locale.forLanguageTag("es")))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
        .andExpect(content().string("Hola Boot! Tested!"));
  }

  @Test
  void testBeansAccess() throws Exception {
    this.mockMvc.perform(get("/beans.action"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
        .andExpect(content().string("beans:bar"));
  }

  @Test
  void testResponseAccess() throws Exception {
    this.mockMvc.perform(get("/response.action"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
        .andExpect(content().string("response:200"));
  }
}
