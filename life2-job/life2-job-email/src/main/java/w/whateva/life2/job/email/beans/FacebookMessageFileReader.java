package w.whateva.life2.job.email.beans;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;
import org.springframework.core.io.Resource;
import w.whateva.life2.xml.email.facebook.FacebookMessage;
import w.whateva.life2.xml.email.facebook.FacebookMessageFile;
import w.whateva.life2.xml.email.facebook.FacebookMessageThread;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class FacebookMessageFileReader
        extends AbstractItemStreamItemReader<FacebookMessageThread>
        implements ItemReader<FacebookMessageThread>, ResourceAwareItemReaderItemStream<FacebookMessageThread> {

    private FacebookMessageFile file;
    private Iterator<Map.Entry<LocalDate, List<FacebookMessage>>> messageIterator;
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

        try {
            file = objectMapper.readValue(resource.getInputStream(), new TypeReference<FacebookMessageFile>() {});
            Map<LocalDate, List<FacebookMessage>> messagesByDate = file.getMessages()
                    .stream()
                    .collect(Collectors.groupingBy(o ->
                            LocalDateTime.ofInstant(o.getTimestamp_ms().toInstant(), ZoneId.of("UTC")).toLocalDate()));

            messageIterator = messagesByDate.entrySet().iterator();
            return;

        } catch (IOException e) {
            e.printStackTrace();
        }

        messageIterator = Collections.emptyIterator();
    }

    @Override
    public FacebookMessageThread read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        init();

        if (!messageIterator.hasNext()) return null;

        Map.Entry<LocalDate, List<FacebookMessage>> entry = messageIterator.next();
        FacebookMessageThread result = new FacebookMessageThread();
        result.setMessages(entry.getValue().stream().sorted(Comparator.comparing(FacebookMessage::getTimestamp_ms)).collect(Collectors.toList()));
        result.setFile(file);
        return result;
    }

    @Override
    public void setResource(Resource resource) {
        this.initialized = false;
        this.resource = resource;
    }
}
