package io.pebbletemplates.boot.autoconfigure;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.loader.ClasspathLoader;
import io.pebbletemplates.spring.reactive.PebbleReactiveViewResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = Type.REACTIVE)
class PebbleReactiveWebConfiguration extends AbstractPebbleConfiguration {

    @Bean
    @ConditionalOnMissingBean(name = "pebbleReactiveViewResolver")
    PebbleReactiveViewResolver pebbleReactiveViewResolver(PebbleProperties properties,
                                                          PebbleEngine pebbleEngine) {
        PebbleReactiveViewResolver resolver = new PebbleReactiveViewResolver(pebbleEngine);
        PropertyMapper map = PropertyMapper.get();
        map.from(() -> {
            String prefix = properties.getPrefix();
            if (pebbleEngine.getLoader() instanceof ClasspathLoader) {
                // classpathloader doesn't like leading slashes in paths
                prefix = this.stripLeadingSlash(properties.getPrefix());
            }
            return prefix;
        }).to(resolver::setPrefix);
        map.from(properties::getSuffix).to(resolver::setSuffix);
        map.from(properties::getViewNames).to(resolver::setViewNames);
        map.from(properties::getRequestContextAttribute).to(resolver::setRequestContextAttribute);
        map.from(properties.getReactive()::getMediaTypes).to(resolver::setSupportedMediaTypes);
        resolver.setOrder(Ordered.LOWEST_PRECEDENCE - 10);

        return resolver;
    }
}
