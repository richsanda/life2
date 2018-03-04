package w.whateva.service.email.data.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;
import w.whateva.service.email.data.domain.Email;
import w.whateva.service.email.data.domain.EmailCount;
import w.whateva.service.email.data.domain.Person;
import w.whateva.service.email.data.repository.PersonDao;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Component
public class PersonDaoImpl implements PersonDao {

    @Autowired
    MongoTemplate mongoTemplate;

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
}
