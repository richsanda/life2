package w.whateva.service.life2.integration.email.netflix;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import static com.fasterxml.jackson.databind.PropertyNamingStrategy.SNAKE_CASE;

public class EmailObjectMapper extends ObjectMapper {

    public EmailObjectMapper() {

        setVisibilityChecker(getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.ANY)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE));

        configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        registerModule(new JavaTimeModule());

        enable(SerializationFeature.INDENT_OUTPUT);

        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        setPropertyNamingStrategy(SNAKE_CASE);
    }
}
