package w.whateva.life2.data.note.repository.impl;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import w.whateva.life2.data.note.NoteDao;
import w.whateva.life2.data.note.domain.Note;
import w.whateva.life2.data.note.domain.NoteMonthYearCount;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Component
public class NoteDaoImpl implements NoteDao {

    @Autowired
    private MongoTemplate mongoTemplate;
    MongoClient client = new MongoClient();
    MongoDatabase db = client.getDatabase("life2");

    @Override
    public List<String> listTroves() {
        List<String> result = new ArrayList<>();
        MongoCursor<String> c =
                db.getCollection("note").distinct("trove", String.class).iterator();
        while (c.hasNext()) {
            result.add(c.next());
        }
        return result;
    }

    @Override
    public List<Note> findByTroveSorted(String trove) {

        Criteria criteria = Criteria.where("trove").is(trove);

        Query query = new Query(criteria).with(Sort.by(Sort.Direction.ASC, "index"));

        return mongoTemplate.find(query, Note.class);
    }

    @Override
    public Note findByTroveAndKey(String trove, String key) {
        return mongoTemplate.findById(composeKey(trove, key), Note.class);
    }

    @Override
    public List<NoteMonthYearCount> getNoteMonthYearCounts(LocalDateTime after, LocalDateTime before, Set<String> who, Set<String> troves) {

        Aggregation agg = newAggregation(
                match(queryCriteria(after, before)),
                project().andExpression("month(sent)").as("month").andExpression("year(sent)").as("year"),
                group("month", "year").count().as("count"),
                sort(Sort.Direction.ASC, "year", "month")
        );

        //Convert the aggregation result into a List
        AggregationResults<NoteMonthYearCount> groupResults = mongoTemplate.aggregate(agg, Note.class, NoteMonthYearCount.class);

        return groupResults.getMappedResults();
    }

    @Override
    public List<Note> getNotes(Set<String> who, Set<String> from, Set<String> to, LocalDateTime after, LocalDateTime before) {

        Criteria criteria = queryCriteria(after, before);

        // *and* with the whoCriteria if it was provided
        // if (null != whoCriteria) queryCriteria = queryCriteria.andOperator(whoCriteria);

        Query query = new Query(criteria).with(Sort.by(Sort.Direction.ASC, "sent"));

        return mongoTemplate.find(query, Note.class);
    }

    private Criteria queryCriteria(LocalDateTime after, LocalDateTime before) {
        ArrayList<Criteria> criteria = new ArrayList<>();

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

        Criteria[] criteriaArray = new Criteria[criteria.size()];
        criteriaArray = criteria.toArray(criteriaArray);

        return new Criteria().andOperator(criteriaArray);
    }

    private static String composeKey(String trove, String key) {
        return String.format("%s/%s", trove, key).toLowerCase();
    }
}
