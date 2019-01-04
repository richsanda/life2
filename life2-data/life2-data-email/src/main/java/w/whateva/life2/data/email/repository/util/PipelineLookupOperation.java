package w.whateva.life2.data.email.repository.util;

import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.*;

import java.util.Collections;
import java.util.Map;

class PipelineLookupOperation implements FieldsExposingAggregationOperation, FieldsExposingAggregationOperation.InheritsFieldsAggregationOperation {

    private final String from;
    private final String as;
    private final Map<String, String> let;
    private final Document pipeline;

    private final LookupOperation lookupOperation; // not useful except to expose "as"

    private static final String FAKE = "FAKE"; // fake field name for unused lookupOperation

    PipelineLookupOperation(String from, String as, Map<String, String> let, Document pipeline) {

        this.from = from;
        this.as = as;
        this.let = let;
        this.pipeline = pipeline;
        this.lookupOperation = Aggregation.lookup(from, FAKE, FAKE, as); // not useful except to expose "as"
    }

    @Override
    public Document toDocument(AggregationOperationContext context) {

        Document lookupObject = new Document();

        lookupObject.append("from", from);
        lookupObject.append("as", as);
        lookupObject.append("let", let);
        lookupObject.append("pipeline", Collections.singletonList(pipeline));

        return context.getMappedObject(Document.parse(new Document("$lookup", lookupObject).toJson()));
    }

    @Override
    public ExposedFields getFields() {
        return lookupOperation.getFields();
    }

    /*

    {$lookup: {
       from: "email",
       as: "email",
       let: {address: "$emails"},
       pipeline: [
         {$match:
           {$expr: {$and: [
             {$in: ["$$address", "$tos"]},
             {$gt: ["$sent", ISODate("2013-01-01T00:00:00Z")]},
             {$lte: ["$sent", ISODate("2013-04-01T00:00:00Z")]}
           ]}}
         }
       ]
     }}

     */
}
