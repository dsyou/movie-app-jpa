package pl.dsyou.movierating.common.rest.version;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.servlet.mvc.condition.*;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;

/**
 * Customer handler to provide unified api version through application.
 */
public class ApiVersionMappingHandlerMapping extends RequestMappingHandlerMapping {

    private static final String API_PREFIX = "api/";

    private final String prefix;

    public ApiVersionMappingHandlerMapping(String prefix) {
        this.prefix = prefix;
    }

    @Override
    protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
        RequestMappingInfo info = super.getMappingForMethod(method, handlerType);

        if (info != null) {
            ApiVersion methodAnnotation = AnnotationUtils.findAnnotation(method, ApiVersion.class);

            if (methodAnnotation != null) {
                RequestCondition<?> methodCondition = getCustomMethodCondition(method);

                // Concatenate ApiVersion with the usual request mapping
                info = createApiVersionInfo(methodAnnotation, methodCondition).combine(info);
            } else {
                ApiVersion typeAnnotation = AnnotationUtils
                        .findAnnotation(handlerType, ApiVersion.class);
                if (typeAnnotation != null) {
                    RequestCondition<?> typeCondition = getCustomTypeCondition(handlerType);

                    // Concatenate our ApiVersion with the usual request mapping
                    info = createApiVersionInfo(typeAnnotation, typeCondition).combine(info);
                }
            }
        }

        return info;
    }

    private RequestMappingInfo createApiVersionInfo(
            ApiVersion annotation, RequestCondition<?> customCondition
    ) {
        int[] values = annotation.value();
        String[] patterns = new String[values.length];

        for (int i = 0; i < values.length; i++) {
            // Build the URL prefix
            patterns[i] = API_PREFIX + prefix + values[i];
        }

        return new RequestMappingInfo(
                new PatternsRequestCondition(patterns, getUrlPathHelper(), getPathMatcher(),
                        useSuffixPatternMatch(), useTrailingSlashMatch(),
                        getFileExtensions()
                ),
                new RequestMethodsRequestCondition(),
                new ParamsRequestCondition(),
                new HeadersRequestCondition(),
                new ConsumesRequestCondition(),
                new ProducesRequestCondition(),
                customCondition
        );
    }
}
