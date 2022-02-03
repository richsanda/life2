package w.whateva.life2.data.pin.repository.impl;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import w.whateva.life2.data.pin.domain.Pin;
import w.whateva.life2.data.pin.domain.PinMonthYearCount;
import w.whateva.life2.data.pin.repository.PinDao;
import w.whateva.life2.data.pin.repository.PinRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class PinDaoImpl implements PinDao {

    private final PinRepository repository;
    private final MongoTemplate mongoTemplate;

    public PinDaoImpl(PinRepository repository, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<String> listTroves() {
        return StreamSupport.stream(mongoTemplate.getCollection("pin")
                        .distinct("trove", String.class).spliterator(), true)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Pin update(Pin pin) {
        return repository.save(pin);
    }

    @Override
    public List<Pin> search(String owner, Set<String> troves, ZonedDateTime after, ZonedDateTime before) {

        Criteria criteria = queryCriteria(owner, troves, after, before);

        Query query = new Query(criteria).with(Sort.by(Sort.Direction.ASC, "when"));

        return mongoTemplate.find(query, Pin.class);
    }

    public List<PinMonthYearCount> getPinMonthYearCounts(Set<String> who, Set<String> from, Set<String> to, LocalDateTime after, LocalDateTime before) {

        Aggregation agg = newAggregation(
                match(queryCriteria(null, Collections.emptySet(), after.atZone(ZoneId.of("UTC")), before.atZone(ZoneId.of("UTC")))),
                project().andExpression("month(when)").as("month").andExpression("year(when)").as("year"),
                group("month", "year").count().as("count"),
                sort(Sort.Direction.ASC, "year", "month")
        );

        //Convert the aggregation result into a List
        AggregationResults<PinMonthYearCount> groupResults = mongoTemplate.aggregate(agg, Pin.class, PinMonthYearCount.class);

        return groupResults.getMappedResults();
    }

    private Criteria queryCriteria(String owner, Set<String> troves, ZonedDateTime after, ZonedDateTime before) {

        ArrayList<Criteria> criteria = new ArrayList<>();

        if (null != owner) {
            criteria.add(Criteria.where("owner").is(owner));
        }

        if (!CollectionUtils.isEmpty(troves)) {
            criteria.add(Criteria.where("troves").in(troves));
        }

        if (null != after || null != before) {
            ArrayList<Criteria> whenCriteriaList = new ArrayList<>();
            if (null != after) {
                whenCriteriaList.add(Criteria.where("when").gte(after));
            }
            if (null != before) {
                whenCriteriaList.add(Criteria.where("when").lt(before));
            }
            Criteria[] whenCriteriaArray = new Criteria[whenCriteriaList.size()];
            whenCriteriaArray = whenCriteriaList.toArray(whenCriteriaArray);
            criteria.add(new Criteria().andOperator(whenCriteriaArray));
        }

        Criteria[] criteriaArray = new Criteria[criteria.size()];
        criteriaArray = criteria.toArray(criteriaArray);

        return new Criteria().andOperator(criteriaArray);
    }
}
