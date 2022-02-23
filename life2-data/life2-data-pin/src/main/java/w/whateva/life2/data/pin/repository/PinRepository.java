package w.whateva.life2.data.pin.repository;

import org.bson.Document;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import w.whateva.life2.data.pin.domain.Pin;
import w.whateva.life2.data.pin.domain.PinMonthYearCount;

import java.util.List;

@Repository(value = "pin")
public interface PinRepository extends MongoRepository<Pin, String> {

    List<Pin> findAllByTypeAndTroveAndKey(String type, String trove, String key);

    @Aggregation(
            pipeline = {
                    "{$match : ?0}"
                    ,"{$addFields: { 'num_months': {$divide: [{$subtract : [{$ifNull: ['$when2', '$when']}, '$when']}, 2592000000]} } }"
                    ,"   {$project: {" +
                    "       title : true," +
                    "       num_months : true," +
                    "       month_years: {" +
                    "          $map: {" +
                    "            input: { $range: [{$month: '$when'}, {$add: [{$toInt: '$num_months'}, {$month: '$when'}, 1]}, 1] }," +
                    "            as: 'dd'," +
                    "            in: {year : {$add: [{$year: '$when'}, {$floor: {$divide: ['$$dd', 12]}}]}, month: {$mod: ['$$dd', 12]}}" +
                    "          }" +
                    "        }" +
                    "      }}"
                    ,"    {$unwind: '$month_years'}"
                    ,"    {$group: {_id : '$month_years', 'count': {$sum: {$divide: [1, { $max : [1, '$num_months']}]}}, items: {$addToSet: '$title'}}}"
                    ,"    {$project: {year: {$cond : [{$eq : [0, '$_id.month']}, {$subtract: ['$_id.year', 1]}, '$_id.year']}, month: {$cond : [{$eq : [0, '$_id.month']}, 12, '$_id.month']}, count: true }}"
                    ,"    {$sort: { _id: 1 }}"
            }
    )
    List<PinMonthYearCount> aggregateMonthYearCounts(Document matchCriteria);
}