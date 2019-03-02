package w.whateva.life2.xml.email.facebook;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.Test;

import java.io.IOException;

public class FacebookMessageTest {

    private static final String message = "    {\n" +
            "      \"sender_name\": \"Rich Sanda\",\n" +
            "      \"timestamp_ms\": 1539283867172,\n" +
            "      \"content\": \"nick, are you aware ? that bodysnatchers is radiohead's best song ?\",\n" +
            "      \"type\": \"Generic\"\n" +
            "    }";

    @Test
    public void test() throws IOException {

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);

        FacebookMessage facebookMessage = objectMapper.readValue(message, new TypeReference<FacebookMessage>() {});

        System.out.println(facebookMessage.getTimestamp_ms());
    }
}
