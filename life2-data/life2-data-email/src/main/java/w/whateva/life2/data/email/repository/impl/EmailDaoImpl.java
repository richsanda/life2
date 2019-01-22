package w.whateva.life2.data.email.repository.impl;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import w.whateva.life2.data.email.domain.Email;
import w.whateva.life2.data.email.repository.EmailDao;
import w.whateva.life2.data.email.repository.util.AggregationUtility;
import w.whateva.life2.data.person.domain.Person;

import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Component
public class EmailDaoImpl implements EmailDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    /*
    db.person.aggregate([

    {"$unwind": "$emails"},

    {"$group": {"_id": "$name", "emails": {$addToSet: "$emails"}}},

    {"$lookup": {"from": "email", "localField": "emails", "foreignField": "toIndex", "as": "join"}},

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
                lookup("email", "emails", "toIndex", "join"),
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
    public List<Email> getEmails(Set<String> who, Set<String> from, Set<String> to, LocalDateTime after, LocalDateTime before) {

        ArrayList<Criteria> criteria = new ArrayList<>();

        if (null != who) {
            criteria.add(new Criteria().orOperator(Criteria.where("toIndex").in(who), Criteria.where("fromIndex").in(who)));
        }

        if (null != from) {
            criteria.add(Criteria.where("fromIndex").in(from));
        }

        if (null != to) {
            criteria.add(Criteria.where("toIndex").in(to));
        }

        if (null != after || null != before) {
            ArrayList<Criteria> sentCriteriaList = new ArrayList<>();
            if (null != after) {
                sentCriteriaList.add(Criteria.where("sent").gte(after));
            }
            if (null != before) {
                sentCriteriaList.add(Criteria.where("sent").lt(before));
            }
            Criteria[] sentCriteriaArray = new Criteria[sentCriteriaList.size()];
            sentCriteriaArray = sentCriteriaList.toArray(sentCriteriaArray);
            criteria.add(new Criteria().andOperator(sentCriteriaArray));
        }

        Criteria[] queryCriteria = new Criteria[criteria.size()];
        queryCriteria = criteria.toArray(queryCriteria);

        Query query = new Query(new Criteria().andOperator(queryCriteria)).with(new Sort(Sort.Direction.ASC, "sent"));

        return mongoTemplate.find(query, Email.class);

        /*
        AggregationOperation[] args = new AggregationOperation[]{};
        int i = 0;

        Set<String> names = Stream.of(who, from, to)
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        if (!CollectionUtils.isEmpty(names)) {
            args[i++] = match(Criteria.where("name").in(names));
        }

        args[i++] = unwind("emails");
        args[i++] = group("name").addToSet("emails").as("emails");
        args[i++] = lookup("email", "emails", "toIndex", "join");
        args[i++] = unwind("join");

        if (null != after || null != before) {
            Criteria criteria = Criteria.where("join.sent");
            if (null != after) criteria = criteria.gte(after);
            if (null != after) criteria = criteria.lt(before);
            args[i++] = match(criteria);
        }

        args[i++] = sort(Sort.Direction.ASC, "join.sent");

        if (!CollectionUtils.isEmpty(who)) {
            args[i++] = match(Criteria.where("join.").in(names));
        }

        args[i] = project(Fields.fields("name", "join.sent", "join.from", "join.to", "join.subject", "join.body"));


        Aggregation agg = newAggregation(args);

        */

        /*
        Aggregation agg = newAggregation(
                match(Criteria.where("name").in(from)),
                unwind("emails"),
                group("name").addToSet("emails").as("emails"),
                lookup("email", "emails", "fromIndex", "join"),
                unwind("join"),
                match(Criteria.where("join.sent").gte(after).lt(before)),
                // group("_id", "emails").count().as("count"),
                sort(Sort.Direction.ASC, "join.sent"),
                project(Fields.fields("name", "join.key", "join.sent", "join.from", "join.to", "join.subject")) // , "join.body"))
        );

        //Convert the aggregation result into a List
        AggregationResults<Email> groupResults  = mongoTemplate.aggregate(agg, Person.class, Email.class);

        return groupResults.getMappedResults();

                */
    }

    /*

db.email.aggregate([

    {"$group": {"_id": "$fromIndex", "emails": {$addToSet: "$fromIndex"}, "count": {$sum: 1}}},

    {"$unwind": "$_id"},

    {"$lookup": {"from": "person", "localField": "_id", "foreignField": "emails", "as": "person"}},

    {"$unwind": "$person"},

    {"$group": {"_id": "$person.name", "emails": {$addToSet: "$person.emails"}, "count": {$sum: "$count"}}},

    {"$sort": { "count": -1} },

    {$project: {"_id": "$_id", "emails": "$emails", "count": "$count"}},

])

     */

    public List<Person> getSenders() {

        Aggregation agg = newAggregation(
                group("fromIndex").addToSet("fromIndex").as("emails").count().as("count"),
                unwind("_id"),
                lookup("person", "_id", "emails", "person"),
                unwind("person"),
                project("person._id", "person.name", "person.emails", "count"),
                group("name").sum("count").as("count").first("name").as("name").first("emails").as("emails"),
                sort(Sort.Direction.DESC, "count")
        );

        //Convert the aggregation result into a List
        AggregationResults<Person> groupResults  = mongoTemplate.aggregate(agg, Email.class, Person.class);

        return groupResults.getMappedResults();
    }

    private List<Email> getEmails(Set<String> names, EmailRole role, LocalDateTime after, LocalDateTime before) {

        Aggregation agg = newAggregation(
                match(Criteria.where("name").in(names)),
                unwind("emails"),
                group("name").addToSet("emails").as("emails"),
                lookup("email", "emails", fieldForRole(role), "join"),
                unwind("join"),
                match(Criteria.where("join.sent").gte(after).lt(before)),
                sort(Sort.Direction.ASC, "join.sent"),
                project(Fields.fields("name", "join.sent", "join.from", "join.to", "join.subject", "join.body"))
        );

        //Convert the aggregation result into a List
        AggregationResults<Email> groupResults  = mongoTemplate.aggregate(agg, Person.class, Email.class);

        return groupResults.getMappedResults();
    }

    private static String fieldForRole(EmailRole role) {
        switch (role) {
            case FROM: return "fromIndex";
            case TO: return "toIndex";
            default: return "toAndFromIndex";
        }
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
                // lookup("email", "emails", "toIndex", "join"),
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
             {$in: ["$$address", "$toIndex"]},
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

    enum EmailRole {
        FROM, TO, EITHER
    }
}
