package w.whateva.service.life2.service.util;

import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Function;

public class ShredUtility {

    <ItemType> List<List<ItemType>> putInBuckets(List<ItemType> items,
                                                 Function<ItemType, LocalDateTime> function,
                                                 int numBuckets,
                                                 LocalDateTime min,
                                                 LocalDateTime max) {

        return new ShredBucketer<ItemType>(items, function, numBuckets, min, max).getBuckets();
    }
}
