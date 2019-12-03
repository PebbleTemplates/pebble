package com.mitchellbosecke.pebble.boot.autoconfigure;

import com.mitchellbosecke.pebble.boot.ReactiveApplication;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ReactiveApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "spring.main.web-application-type=reactive")
public class ReactiveAppTest {

  @Autowired
  private WebTestClient client;

  @Test
  public void testOk() throws Exception {
    String result = this.client.get().uri("/index.action").exchange()
        .expectStatus().isOk()
        .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
        .expectBody(String.class)
        .returnResult().getResponseBody();

    assertThat(result).isEqualTo("Hello Pebbleworld!");
  }

  @Test
  public void testRequestAccess() throws Exception {
    String result = this.client.get().uri("/contextPath.action").exchange()
        .expectStatus().isOk()
        .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
        .expectBody(String.class)
        .returnResult().getResponseBody();

    assertThat(result).isEqualTo("ctx path:");
  }

  @Test
  public void testEnglishHello() throws Exception {
    String result = this.client.get().uri("/hello.action")
        .header("Accept-Language", "en").exchange()
        .expectStatus().isOk()
        .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
        .expectBody(String.class)
        .returnResult().getResponseBody();

    assertThat(result).isEqualTo("Hello Boot!");
  }

  @Test
  public void testSpanishHello() throws Exception {
    String result = this.client.get().uri("/hello.action")
        .header("Accept-Language", "es").exchange()
        .expectStatus().isOk()
        .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
        .expectBody(String.class)
        .returnResult().getResponseBody();

    assertThat(result).isEqualTo("Hola Boot!");
  }

  @Test
  public void testAdditionalExtensions() throws Exception {
    String result = this.client.get().uri("/extensions.action")
        .header("Accept-Language", "es").exchange()
        .expectStatus().isOk()
        .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_HTML)
        .expectBody(String.class)
        .returnResult().getResponseBody();

    assertThat(result).isEqualTo("Hola Boot! Tested!");
  }
}

