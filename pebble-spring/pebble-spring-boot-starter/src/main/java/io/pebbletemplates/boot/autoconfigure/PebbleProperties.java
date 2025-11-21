package io.pebbletemplates.boot.autoconfigure;

import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.util.MimeType;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

@ConfigurationProperties("pebble")
public class PebbleProperties {

    private static final MimeType DEFAULT_CONTENT_TYPE = MimeType.valueOf("text/html");
    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public static final String DEFAULT_PREFIX = "/templates/";
    public static final String DEFAULT_SUFFIX = ".peb";

    private Locale defaultLocale;
    private boolean strictVariables;
    private boolean greedyMatchMethod;

    private final Servlet servlet = new Servlet(this::getCharset);
    private final Reactive reactive = new Reactive();

    /**
     * View names that can be resolved.
     */
    private String @Nullable [] viewNames;

    /**
     * Name of the RequestContext attribute for all views.
     */
    private @Nullable String requestContextAttribute;

    /**
     * Template encoding.
     */
    private Charset charset = DEFAULT_CHARSET;

    /**
     * Whether to check that the templates location exists.
     */
    private boolean checkTemplateLocation = true;

    /**
     * Prefix to apply to template names.
     */
    private String prefix = DEFAULT_PREFIX;

    /**
     * Suffix to apply to template names.
     */
    private String suffix = DEFAULT_SUFFIX;

    public Servlet getServlet() {
        return this.servlet;
    }

    public Reactive getReactive() {
        return this.reactive;
    }

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

    public String @Nullable [] getViewNames() {
        return this.viewNames;
    }

    public void setViewNames(String @Nullable [] viewNames) {
        this.viewNames = viewNames;
    }

    public @Nullable String getRequestContextAttribute() {
        return this.requestContextAttribute;
    }

    public void setRequestContextAttribute(@Nullable String requestContextAttribute) {
        this.requestContextAttribute = requestContextAttribute;
    }

    public Charset getCharset() {
        return this.charset;
    }

    public String getCharsetName() {
        return this.charset.name();
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    public boolean isCheckTemplateLocation() {
        return this.checkTemplateLocation;
    }

    public void setCheckTemplateLocation(boolean checkTemplateLocation) {
        this.checkTemplateLocation = checkTemplateLocation;
    }

    public static class Servlet {

        /**
         * Whether HttpServletRequest attributes are allowed to override (hide) controller
         * generated model attributes of the same name.
         */
        private boolean allowRequestOverride = false;

        /**
         * Whether HttpSession attributes are allowed to override (hide) controller
         * generated model attributes of the same name.
         */
        private boolean allowSessionOverride = false;

        /**
         * Whether to enable template caching.
         */
        private boolean cache = true;

        /**
         * Content-Type value.
         */
        private MimeType contentType = DEFAULT_CONTENT_TYPE;

        /**
         * Whether all request attributes should be added to the model prior to merging
         * with the template.
         */
        private boolean exposeRequestAttributes = false;

        /**
         * Whether all HttpSession attributes should be added to the model prior to
         * merging with the template.
         */
        private boolean exposeSessionAttributes = false;

        /**
         * Whether to expose a RequestContext for use by Spring's macro library, under the
         * name "springMacroRequestContext".
         */
        private boolean exposeSpringMacroHelpers = true;

        private final Supplier<@Nullable Charset> charset;

        public Servlet() {
            this.charset = () -> null;
        }

        private Servlet(Supplier<@Nullable Charset> charset) {
            this.charset = charset;
        }

        public boolean isAllowRequestOverride() {
            return this.allowRequestOverride;
        }

        public void setAllowRequestOverride(boolean allowRequestOverride) {
            this.allowRequestOverride = allowRequestOverride;
        }

        public boolean isAllowSessionOverride() {
            return this.allowSessionOverride;
        }

        public void setAllowSessionOverride(boolean allowSessionOverride) {
            this.allowSessionOverride = allowSessionOverride;
        }

        public boolean isCache() {
            return this.cache;
        }

        public void setCache(boolean cache) {
            this.cache = cache;
        }

        public MimeType getContentType() {
            if (this.contentType != null && this.contentType.getCharset() == null) {
                Charset charset = this.charset.get();
                if (charset != null) {
                    Map<String, String> parameters = new LinkedHashMap<>();
                    parameters.put("charset", charset.name());
                    parameters.putAll(this.contentType.getParameters());
                    return new MimeType(this.contentType, parameters);
                }
            }
            return this.contentType;
        }

        public void setContentType(MimeType contentType) {
            this.contentType = contentType;
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

        public boolean isExposeSpringMacroHelpers() {
            return this.exposeSpringMacroHelpers;
        }

        public void setExposeSpringMacroHelpers(boolean exposeSpringMacroHelpers) {
            this.exposeSpringMacroHelpers = exposeSpringMacroHelpers;
        }

    }

    public static class Reactive {

        /**
         * Media types supported by Mustache views.
         */
        private @Nullable List<MediaType> mediaTypes;

        public @Nullable List<MediaType> getMediaTypes() {
            return this.mediaTypes;
        }

        public void setMediaTypes(@Nullable List<MediaType> mediaTypes) {
            this.mediaTypes = mediaTypes;
        }

    }

    public Locale getDefaultLocale() {
        return this.defaultLocale;
    }

    public void setDefaultLocale(Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public boolean isStrictVariables() {
        return this.strictVariables;
    }

    public void setStrictVariables(boolean strictVariables) {
        this.strictVariables = strictVariables;
    }

    public boolean isGreedyMatchMethod() {
        return this.greedyMatchMethod;
    }

    public void setGreedyMatchMethod(boolean greedyMatchMethod) {
        this.greedyMatchMethod = greedyMatchMethod;
    }
}
