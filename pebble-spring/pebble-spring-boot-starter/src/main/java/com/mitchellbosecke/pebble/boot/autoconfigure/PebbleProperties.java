package com.mitchellbosecke.pebble.boot.autoconfigure;

import java.nio.charset.Charset;
import java.util.Locale;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.MimeType;

@ConfigurationProperties("pebble")
public class PebbleProperties {

  private static final Charset DEFAULT_ENCODING = Charset.forName("UTF-8");

  private static final MimeType DEFAULT_CONTENT_TYPE = MimeType.valueOf("text/html");

  public static final String DEFAULT_PREFIX = "/templates/";

  public static final String DEFAULT_SUFFIX = ".pebble";

  private String prefix = DEFAULT_PREFIX;

  private String suffix = DEFAULT_SUFFIX;

  private Charset encoding = DEFAULT_ENCODING;

  private MimeType contentType = DEFAULT_CONTENT_TYPE;

  private boolean cache = true;

  private boolean exposeRequestAttributes;

  private boolean exposeSessionAttributes;

  private Locale defaultLocale;

  private boolean strictVariables = false;

  public String getPrefix() {
    return this.prefix;
  }

  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }

  public String getSuffix() {
    return this.suffix;
  }

  public void setSuffix(String suffix) {
    this.suffix = suffix;
  }

  public Charset getEncoding() {
    return this.encoding;
  }

  public void setEncoding(Charset encoding) {
    this.encoding = encoding;
  }

  public MimeType getContentType() {
    return this.contentType;
  }

  public void setContentType(MimeType contentType) {
    this.contentType = contentType;
  }

  public boolean isCache() {
    return this.cache;
  }

  public void setCache(boolean cache) {
    this.cache = cache;
  }

  public boolean isExposeRequestAttributes() {
    return this.exposeRequestAttributes;
  }

  public void setExposeRequestAttributes(boolean exposeRequestAttributes) {
    this.exposeRequestAttributes = exposeRequestAttributes;
  }

  public boolean isExposeSessionAttributes() {
    return this.exposeSessionAttributes;
  }

  public void setExposeSessionAttributes(boolean exposeSessionAttributes) {
    this.exposeSessionAttributes = exposeSessionAttributes;
  }

  public Locale getDefaultLocale() {
    return this.defaultLocale;
  }

  public void setDefaultLocale(Locale defaultLocale) {
    this.defaultLocale = defaultLocale;
  }

  public boolean isStrictVariables() {
    return strictVariables;
  }

  public void setStrictVariables(boolean strictVariables) {
    this.strictVariables = strictVariables;
  }
}
