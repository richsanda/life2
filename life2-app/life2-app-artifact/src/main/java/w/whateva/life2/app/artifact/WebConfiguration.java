package w.whateva.life2.app.artifact;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import w.whateva.life2.integration.api.ArtifactProvider;

@Configuration
public class WebConfiguration implements WebMvcRegistrations {

    @Override
    public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {
        RequestMappingHandlerMapping mapping = new RequestMappingHandlerMapping() {
            @Override
            protected boolean isHandler(Class<?> beanType) {
                return super.isHandler(beanType) && !beanType.isAssignableFrom(ArtifactProvider.class);
                // return super.isHandler(beanType) && (AnnotationUtils.findAnnotation(beanType, FeignClient.class) == null);
            }
        };
        return mapping;
    }
}
