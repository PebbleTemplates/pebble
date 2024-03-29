/*
 * Copyright (c) 2013 by Mitchell Bösecke
 *
 * For the full copyright and license information, please view the LICENSE
 * file that was distributed with this source code.
 */
package io.pebbletemplates.spring;

import io.pebbletemplates.spring.config.MVCConfig;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ViewResolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit test for the PebbleViewResolver
 *
 * @author Eric Bussieres
 */
@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration(classes = MVCConfig.class)
class PebbleViewResolverTest {

  private static final String CONTEXT_PATH = "/testContextPath";
  private static final Locale DEFAULT_LOCALE = Locale.CANADA;
  private static final String EXPECTED_RESPONSE_PATH = "/io/pebbletemplates/spring/expectedResponse";
  private static final String FORM_NAME = "formName";

  private BindingResult mockBindingResult = mock(BindingResult.class);
  private MockHttpServletRequest mockRequest = new MockHttpServletRequest();
  private MockHttpServletResponse mockResponse = new MockHttpServletResponse();

  @Autowired
  private ViewResolver viewResolver;

  @BeforeEach
  void initRequest() {
    this.mockRequest.setContextPath(CONTEXT_PATH);
    this.mockRequest.getSession().setMaxInactiveInterval(600);

    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(this.mockRequest));
  }

  @BeforeEach
  void initBindingResult() {
    this.initBindingResultAllErrors();
    this.initBindingResultGlobalErrors();
    this.initBindingResultFieldErrors();
  }

  private void initBindingResultAllErrors() {
    when(this.mockBindingResult.hasErrors()).thenReturn(true);

    List<ObjectError> allErrors = new ArrayList<>();
    allErrors.add(
        new ObjectError(FORM_NAME, new String[]{"error.test"}, new String[]{}, "???error.test???"));
    when(this.mockBindingResult.getAllErrors()).thenReturn(allErrors);
  }

  private void initBindingResultGlobalErrors() {
    when(this.mockBindingResult.hasGlobalErrors()).thenReturn(true);

    List<ObjectError> globalErrors = new ArrayList<>();
    globalErrors.add(new ObjectError(FORM_NAME, new String[]{"error.global.test.params"},
        new String[]{"param1", "param2"}, "???error.global.test.params???"));
    when(this.mockBindingResult.getGlobalErrors()).thenReturn(globalErrors);
  }

  private void initBindingResultFieldErrors() {
    when(this.mockBindingResult.hasFieldErrors("testField")).thenReturn(true);

    List<FieldError> fieldErrors = new ArrayList<>();
    fieldErrors.add(
        new FieldError(FORM_NAME, "testField", null, false, new String[]{"error.field.test.params"},
            new String[]{"param1", "param2"}, "???error.field.test.params???"));
    when(this.mockBindingResult.getFieldErrors("testField")).thenReturn(fieldErrors);
  }

  @Test
  void whenRenderingAPage_givenPageWithBeanVariable_thenRenderingIsOK() throws Exception {
    String result = this.render("beansTest", new HashMap<String, Object>());

    this.assertOutput(result, EXPECTED_RESPONSE_PATH + "/beansTest.html");
  }

  @Test
  void whenRenderingAPage_givenPageWithBindingResult_thenRenderingIsOK() throws Exception {
    Map<String, Object> model = this.givenBindingResult();

    String result = this.render("bindingResultTest", model);

    this.assertOutput(result, EXPECTED_RESPONSE_PATH + "/bindingResultTest.html");
  }

  private Map<String, Object> givenBindingResult() {
    Map<String, Object> model = new HashMap<>();
    model.put(BindingResult.MODEL_KEY_PREFIX + FORM_NAME, this.mockBindingResult);
    return model;
  }

  @Test
  void whenRenderingAPage_givenPageWithBindingResultAndMacro_thenRenderingIsOK() throws Exception {
    Map<String, Object> model = this.givenBindingResult();

    String result = this.render("bindingResultWithMacroTest", model);

    this.assertOutput(result, EXPECTED_RESPONSE_PATH + "/bindingResultWithMacroTest.html");
  }

  @Test
  void whenRenderingAPage_givenPageWithHrefFunction_thenRenderingIsOK() throws Exception {
    String result = this.render("hrefFunctionTest", new HashMap<String, Object>());

    this.assertOutput(result, EXPECTED_RESPONSE_PATH + "/hrefFunctionTest.html");
  }

  @Test
  void whenRenderingAPageInEnglish_givenPageWithResourceBundleMessage_thenRenderingIsOK()
      throws Exception {
    String result = this.render("messageEnTest", new HashMap<String, Object>());

    this.assertOutput(result, EXPECTED_RESPONSE_PATH + "/messageEnTest.html");
  }

  @Test
  void whenRenderingAPageInFrench_givenPageWithResourceBundleMessage_thenRenderingIsOK()
      throws Exception {
    this.mockRequest.addPreferredLocale(Locale.CANADA_FRENCH);

    String result = this.render("messageFrTest", new HashMap<String, Object>());

    this.assertOutput(result, EXPECTED_RESPONSE_PATH + "/messageFrTest.html");
  }

  @Test
  void whenRenderingAPage_givenPageWithHttpRequestVariable_thenRenderingIsOK()
      throws Exception {
    String result = this.render("requestTest", new HashMap<String, Object>());

    this.assertOutput(result, EXPECTED_RESPONSE_PATH + "/requestTest.html");
  }

  @Test
  void whenRenderingAPage_givenPageWithHttpResponseVariable_thenRenderingIsOK()
      throws Exception {
    String result = this.render("responseTest", new HashMap<String, Object>());

    this.assertOutput(result, EXPECTED_RESPONSE_PATH + "/responseTest.html");
  }

  @Test
  void whenRenderingAPage_givenPageWithHttpSessionVariable_thenRenderingIsOK()
      throws Exception {
    String result = this.render("sessionTest", new HashMap<String, Object>());

    this.assertOutput(result, EXPECTED_RESPONSE_PATH + "/sessionTest.html");
  }

  private void assertOutput(String output, String expectedOutput) throws IOException {
    assertEquals(this.readExpectedOutputResource(expectedOutput), output.replaceAll("\\s", ""));
  }

  private String readExpectedOutputResource(String expectedOutput) throws IOException {
    BufferedReader reader = new BufferedReader(
        new InputStreamReader(this.getClass().getResourceAsStream(expectedOutput)));

    StringBuilder builder = new StringBuilder();
    for (String currentLine = reader.readLine(); currentLine != null;
        currentLine = reader.readLine()) {
      builder.append(currentLine);
    }

    return this.removeAllWhitespaces(builder.toString());
  }

  private String removeAllWhitespaces(String source) {
    return source.replaceAll("\\s", "");
  }

  private String render(String location, Map<String, ?> model) throws Exception {
    this.viewResolver.resolveViewName(location, DEFAULT_LOCALE)
        .render(model, this.mockRequest, this.mockResponse);
    return this.mockResponse.getContentAsString();
  }
}
