package w.whateva;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;

/**
 * Unit test for simple EmailApplication.
 */
public class EmailApplicationTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public EmailApplicationTest(String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( EmailApplicationTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp()
    {
        assertTrue( true );
    }

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

    public void testQuery() {

        ApplicationContext ctx = new AnnotationConfigApplicationContext(EmailApplicationTest.class);
        MongoOperations mongoOperation = (MongoOperations) ctx.getBean("mongoTemplate");

        AggregationOperation match = Aggregation.match(Criteria.where("name").is("bill"));
        AggregationOperation unwind = Aggregation.unwind("emails");
        // AggregationOperation group = Aggregation.group();
        AggregationOperation match2 = Aggregation.match(Criteria.where("myDetails.type").is("health"));
        AggregationOperation sort = Aggregation.sort(Sort.Direction.ASC, "myDetails.datetime");
        AggregationOperation limit = Aggregation.limit(1);

        Aggregation aggregation = Aggregation.newAggregation(match, unwind, match2, sort, limit);
        System.out.println("Aggregation = "+aggregation);
        AggregationResults<AggregateFactoryResult> output = mongoOperation.aggregate(aggregation, "gui_data", AggregateFactoryResult.class);
        System.out.println("output = " + output.getMappedResults());
    }

    private class AggregateFactoryResult {

    }
}
