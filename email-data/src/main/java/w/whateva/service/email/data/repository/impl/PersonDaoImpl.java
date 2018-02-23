package w.whateva.service.email.data.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Component;
import w.whateva.service.email.data.domain.EmailCount;
import w.whateva.service.email.data.domain.Person;
import w.whateva.service.email.data.repository.PersonDao;

import java.util.List;

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
                group("_id", "emails").count().as("count"),
                sort(Sort.Direction.DESC, "count")
                );

        //Convert the aggregation result into a List
        AggregationResults<EmailCount> groupResults
                = mongoTemplate.aggregate(agg, Person.class, EmailCount.class);

        List<EmailCount> result = groupResults.getMappedResults();

        return result;

    }

}
