package w.whateva.life2.app.email;

import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

@Configuration
public class EmailConfiguration {

    @Bean
    public WebMvcRegistrations serviceWebRegistrations() {

        return new WebMvcRegistrations() {

            @Override
            public RequestMappingHandlerMapping getRequestMappingHandlerMapping() {

                return new RequestMappingHandlerMapping() {

                    @Override
                    protected boolean isHandler(Class<?> beanType) {
                        return super.isHandler(beanType) && (AnnotationUtils.findAnnotation(beanType, RestController.class) != null);
                    }
                };
            }
        };
    }
}
