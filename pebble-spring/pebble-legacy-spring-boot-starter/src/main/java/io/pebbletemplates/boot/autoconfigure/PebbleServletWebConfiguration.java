package io.pebbletemplates.boot.autoconfigure;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.attributes.methodaccess.AllowlistMethodAccessValidator;
import io.pebbletemplates.pebble.attributes.methodaccess.MethodAccessValidator;
import io.pebbletemplates.pebble.loader.ClasspathLoader;
import io.pebbletemplates.spring.servlet.PebbleViewResolver;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = Type.SERVLET)
class PebbleServletWebConfiguration extends AbstractPebbleConfiguration {

    @Bean
    @ConditionalOnMissingBean(MethodAccessValidator.class)
    MethodAccessValidator pebbleServletMethodAccessValidator(PebbleProperties properties) {
        AllowlistMethodAccessValidator.Builder builder = AllowlistMethodAccessValidator.builder().allowDefaults()
                .allowMethod(HttpServletRequest.class, "getContextPath")
                .allowMethod(HttpServletResponse.class, "getStatus", "getContentType",
                        "getCharacterEncoding")
                .allowMethod(HttpSession.class, "getMaxInactiveInterval");
        if (properties.getAllowPackages() != null) {
            builder.allowPackages(properties.getAllowPackages());
        }
        return builder.build();
    }

  @Bean
  @ConditionalOnMissingBean(name = "pebbleViewResolver")
  PebbleViewResolver pebbleViewResolver(PebbleProperties properties,
                                        PebbleEngine pebbleEngine) {
    PebbleViewResolver pvr = new PebbleViewResolver(pebbleEngine);
    properties.applyToMvcViewResolver(pvr);
    if (pebbleEngine.getLoader() instanceof ClasspathLoader) {
      // classpathloader doesn't like leading slashes in paths
      pvr.setPrefix(this.stripLeadingSlash(properties.getPrefix()));
    }

    return pvr;
  }
}
