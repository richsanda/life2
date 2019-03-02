package w.whateva.life2.job.email.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.collect.Lists;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;
import org.springframework.core.io.Resource;
import w.whateva.life2.xml.email.facebook.FacebookMessage;
import w.whateva.life2.xml.email.facebook.FacebookMessageContext;
import w.whateva.life2.xml.email.facebook.FacebookMessageFile;
import w.whateva.life2.xml.email.facebook.FacebookMessageParticipant;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class FacebookMessageFileReader
        extends AbstractItemStreamItemReader<FacebookMessageContext>
        implements ItemReader<FacebookMessageContext>, ResourceAwareItemReaderItemStream<FacebookMessageContext> {

    private FacebookMessageFile file;
    private Iterator<FacebookMessage> messageIterator;
    private boolean initialized = false;
    private Resource resource;

    private void init() {

        if (initialized) return;

        initialized = true;

        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);

/*        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // from: http://stackoverflow.com/questions/28802544/java-8-localdate-jackson-format
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATE_TIMESTAMPS_AS_NANOSECONDS, false);*/

        try {
            file = objectMapper.readValue(resource.getInputStream(), new TypeReference<FacebookMessageFile>() {});
            messageIterator = file.getMessages().iterator();
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
        messageIterator = Lists.<FacebookMessage>newArrayList().iterator();
    }

    @Override
    public FacebookMessageContext read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        init();
        if (!messageIterator.hasNext()) return null;
        FacebookMessageContext result = new FacebookMessageContext();
        result.setMessage(messageIterator.next());
        result.setFile(file);
        return result;
    }

    @Override
    public void setResource(Resource resource) {
        this.initialized = false;
        this.resource = resource;
    }
}
