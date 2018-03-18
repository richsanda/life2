package w.whateva.service.life2.service.util;

import org.springframework.stereotype.Service;
import w.whateva.service.life2.service.util.bucket.Operator;
import w.whateva.service.life2.service.util.bucket.BucketDistributor;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShredUtility {

    public <ItemType> List<List<ItemType>> putInBuckets(List<ItemType> items,
                                                        Operator<ItemType, LocalDateTime> operator,
                                                        int numBuckets,
                                                        LocalDateTime min,
                                                        LocalDateTime max) {

        return new BucketDistributor<>(items, operator, numBuckets, min, max).getBuckets();
    }
}
