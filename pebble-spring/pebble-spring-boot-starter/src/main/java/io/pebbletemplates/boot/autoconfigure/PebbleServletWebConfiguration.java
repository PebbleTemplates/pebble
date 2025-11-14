package io.pebbletemplates.boot.autoconfigure;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.ClasspathLoader;
import io.pebbletemplates.spring.servlet.PebbleViewResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = Type.SERVLET)
class PebbleServletWebConfiguration extends AbstractPebbleConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "pebbleViewResolver")
    PebbleViewResolver pebbleViewResolver(PebbleProperties properties,
                                          PebbleEngine pebbleEngine) {
        PebbleViewResolver pvr = new PebbleViewResolver(pebbleEngine);

        String prefix = properties.getPrefix();
        if (pebbleEngine.getLoader() instanceof ClasspathLoader) {
            // classpathloader doesn't like leading slashes in paths
            prefix = this.stripLeadingSlash(properties.getPrefix());
        }
        pvr.setPrefix(prefix);
        pvr.setSuffix(properties.getSuffix());
        pvr.setCache(properties.getServlet().isCache());
        if (properties.getServlet().getContentType() != null) {
            pvr.setContentType(properties.getServlet().getContentType().toString());
        }
        pvr.setViewNames(properties.getViewNames());
        pvr.setExposeRequestAttributes(properties.getServlet().isExposeRequestAttributes());
        pvr.setAllowRequestOverride(properties.getServlet().isAllowRequestOverride());
        pvr.setAllowSessionOverride(properties.getServlet().isAllowSessionOverride());
        pvr.setExposeSessionAttributes(properties.getServlet().isExposeSessionAttributes());
        pvr.setExposeSpringMacroHelpers(properties.getServlet().isExposeSpringMacroHelpers());
        pvr.setRequestContextAttribute(properties.getRequestContextAttribute());
        pvr.setCharacterEncoding(properties.getCharsetName());
        pvr.setOrder(Ordered.LOWEST_PRECEDENCE - 20);

        return pvr;
    }
}
