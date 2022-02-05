package w.whateva.life2.data.pin.repository.impl;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import w.whateva.life2.api.person.PersonService;
import w.whateva.life2.data.pin.domain.Pin;
import w.whateva.life2.data.pin.domain.PinMonthYearCount;
import w.whateva.life2.data.pin.repository.PinDao;
import w.whateva.life2.data.pin.repository.PinRepository;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Service
public class PinDaoImpl implements PinDao {

    private final PinRepository repository;
    private final MongoTemplate mongoTemplate;
    private final PersonService personService;

    public PinDaoImpl(PinRepository repository, MongoTemplate mongoTemplate, PersonService personService) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
        this.personService = personService;
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
    public List<Pin> search(ZonedDateTime after, ZonedDateTime before, Set<String> who, Set<String> troves) {

        Criteria criteria = queryCriteria(after, before, who, troves);

        Query query = new Query(criteria).with(Sort.by(Sort.Direction.ASC, "when"));

        return mongoTemplate.find(query, Pin.class);
    }

    public List<PinMonthYearCount> getPinMonthYearCounts(LocalDateTime after, LocalDateTime before, Set<String> who, Set<String> troves) {

        Aggregation agg = newAggregation(
                match(queryCriteria(after.atZone(ZoneId.of("UTC")), before.atZone(ZoneId.of("UTC")), who, troves)),
                project().andExpression("month(when)").as("month").andExpression("year(when)").as("year"),
                group("month", "year").count().as("count"),
                sort(Sort.Direction.ASC, "year", "month")
        );

        //Convert the aggregation result into a List
        AggregationResults<PinMonthYearCount> groupResults = mongoTemplate.aggregate(agg, Pin.class, PinMonthYearCount.class);

        return groupResults.getMappedResults();
    }

    private Criteria queryCriteria(ZonedDateTime after, ZonedDateTime before, Set<String> who, Set<String> troves) {

        ArrayList<Criteria> criteria = new ArrayList<>();

        if (!CollectionUtils.isEmpty(who)) {
            Set<String> emailAddresses = personService.findEmailAddresses(who);
            ArrayList<Criteria> whoCriteriaList = new ArrayList<>();
            whoCriteriaList.add(Criteria.where("data.who").in(who));
            whoCriteriaList.add(Criteria.where("from").in(emailAddresses));
            whoCriteriaList.add(Criteria.where("to").in(emailAddresses));
            Criteria[] whoCriteriaArray = new Criteria[whoCriteriaList.size()];
            whoCriteriaArray = whoCriteriaList.toArray(whoCriteriaArray);
            criteria.add(new Criteria().orOperator(whoCriteriaArray));        }

        if (!CollectionUtils.isEmpty(troves)) {
            criteria.add(Criteria.where("trove").in(troves));
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
