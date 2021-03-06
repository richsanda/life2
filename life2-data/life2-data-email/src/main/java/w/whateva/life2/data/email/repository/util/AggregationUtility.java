package w.whateva.life2.data.email.repository.util;

import org.bson.Document;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

public class AggregationUtility {

    public static PipelineLookupOperation lookup(String from, String as, Map<String, String> let, Document pipeline) {
        return new PipelineLookupOperation(from, as, let, pipeline);
    }

    public static Document emailPipelineAsDoc(String as, ZonedDateTime start, ZonedDateTime end) {
        return new EmailPipelineBuilder(as, start, end).toDocument();
    }
}
