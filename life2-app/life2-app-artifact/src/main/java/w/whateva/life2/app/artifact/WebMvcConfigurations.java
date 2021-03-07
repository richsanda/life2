package w.whateva.life2.app.artifact;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfigurations implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
                .addResourceHandler("/w/neat/**")
                .addResourceLocations("file:/Volumes/20200915/rich-20200910/rich/life2/data/neat-2021-02-25/");
    }
}
