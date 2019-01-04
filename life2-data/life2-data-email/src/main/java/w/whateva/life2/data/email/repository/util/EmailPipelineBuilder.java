package w.whateva.life2.data.email.repository.util;

import org.bson.Document;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class EmailPipelineBuilder {

    private static final String MATCH = "$match";
    private static final String EXPR = "$expr";
    private static final String AND = "$and";
    private static final String IN = "$in";
    private static final String GT = "$gt";
    private static final String LTE = "$lte";

    private final String as;
    private final LocalDateTime after;
    private final LocalDateTime before;

    EmailPipelineBuilder(String as, LocalDateTime after, LocalDateTime before) {
        this.as = as;
        this.after = after;
        this.before = before;
    }

    public Document toDocument() {

        Document match = new Document();
        Document expr = new Document();
        Document and = new Document();
        List<Document> criteria = new ArrayList<>();

        match.append(MATCH, expr);
        expr.append(EXPR, and);
        and.append(AND, criteria);

        Document in = new Document();
        in.append(IN, Arrays.asList("$from", "$$addresses"));
        criteria.add(in);

        if (null != after) {
            Document doc = new Document();
            doc.append(GT, Arrays.asList("$sent", Date.from(after.toInstant(ZoneOffset.UTC))));
            criteria.add(doc);
        }

        if (null != before) {
            Document doc = new Document();
            doc.append(LTE, Arrays.asList("$sent", Date.from(before.toInstant(ZoneOffset.UTC))));
            criteria.add(doc);
        }

        return match;
    }

    private static String formatDate(ZonedDateTime dateTime) {
        return String.format("ISODate(%s)", dateTime.format(DateTimeFormatter.ISO_DATE_TIME));
    }


    /*

         {$match:
           {$expr: {$and: [
             {$in: ["$$address", "$tos"]},
             {$gt: ["$sent", ISODate("2013-01-01T00:00:00Z")]},
             {$lte: ["$sent", ISODate("2013-04-01T00:00:00Z")]}
           ]}}

     */
}
