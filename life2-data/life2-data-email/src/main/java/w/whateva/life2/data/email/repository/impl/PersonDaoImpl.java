package w.whateva.life2.data.email.repository.impl;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;
import w.whateva.life2.data.email.domain.Email;
import w.whateva.life2.data.email.domain.Person;
import w.whateva.life2.data.email.repository.PersonDao;
import w.whateva.life2.data.email.repository.util.AggregationUtility;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Component
public class PersonDaoImpl implements PersonDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    /*
    db.person.aggregate([

    {"$unwind": "$emails"},

    {"$group": {"_id": "$name", "emails": {$addToSet: "$emails"}}},

    {"$lookup": {"from": "email", "localField": "emails", "foreignField": "tos", "as": "join"}},

    {"$unwind": "$join"},

    {"$group": {"_id": "$_id", "count": {$sum: 1}}},

    {"$sort": { "count": -1} },

    ])
     */

    /*
    @Override
    public List<EmailCount> getEmailCount() {

        Aggregation agg = newAggregation(
                //match(Criteria.where("name").is("rich.s")),
                unwind("emails"),
                group("name").addToSet("emails").as("emails"),
                lookup("email", "emails", "tos", "join"),
                unwind("join"),
                group("_id", "join").count().as("count"),
                sort(Sort.Direction.DESC, "count")
                );

        //Convert the aggregation result into a List
        AggregationResults<EmailCount> groupResults  = mongoTemplate.aggregate(agg, Person.class, EmailCount.class);

        return groupResults.getMappedResults();
    }
    */

    @Override
    public List<Email> getEmails(Set<String> names, LocalDateTime after, LocalDateTime before) {

        Aggregation agg = newAggregation(
                match(Criteria.where("name").in(names)),
                unwind("emails"),
                group("name").addToSet("emails").as("emails"),
                lookup("email", "emails", "tos", "join"),
                unwind("join"),
                match(Criteria.where("join.sent").gte(after).lt(before)),
                // group("_id", "emails").count().as("count"),
                sort(Sort.Direction.ASC, "join.sent"),
                project(Fields.fields("name", "join.sent", "join.from", "join.to", "join.subject", "join.body"))
        );

        //Convert the aggregation result into a List
        AggregationResults<Email> groupResults  = mongoTemplate.aggregate(agg, Person.class, Email.class);

        return groupResults.getMappedResults();
    }


    //@Override
    public List<Email> getEmailsUsingPipeline(Set<String> names, LocalDateTime after, LocalDateTime before) {

        Map<String, String> let = new HashMap<>();
        let.put("address", "$emails");

        Document emailPipeline = AggregationUtility.emailPipelineAsDoc("address", after, before);

        Aggregation agg = newAggregation(
                match(Criteria.where("name").in(names)),
                unwind("emails"),
                // group("name").addToSet("emails").as("emails"),
                // lookup("email", "emails", "tos", "join"),
                AggregationUtility.lookup("email", "join", let, emailPipeline),
                //new JsonAggregationOperation(query),
                unwind("join"),
                // match(Criteria.where("join.sent").gte(after).lt(before)),
                // group("_id", "emails").count().as("count"),
                sort(Sort.Direction.ASC, "join.sent"),
                project(Fields.fields("join", "join.sent", "join.from", "join.to", "join.subject", "join.body"))
        ).withOptions(newAggregationOptions().
                allowDiskUse(true).build());

        //Convert the aggregation result into a List
        AggregationResults<Email> groupResults  = mongoTemplate.aggregate(agg, Person.class, Email.class);

        return groupResults.getMappedResults();
    }

    /*

    db.person.aggregate([

    {$match: {name: {$in: ["rich.s"]}}},

    {$unwind: "$emails"},

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
     }},

    {$unwind: "$email"},

    {$sort: {"email.sent": 1}},

    {$project: {"_id": "$email.id", "sent": "$email.sent", "from": "$email.from", "to": "$email.to", "subject": "$email.subject"}}
]);



     */
}
