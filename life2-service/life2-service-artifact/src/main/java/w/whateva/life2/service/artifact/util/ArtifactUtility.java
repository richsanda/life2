package w.whateva.life2.service.artifact.util;

import org.springframework.stereotype.Service;
import w.whateva.life2.service.artifact.util.bucket.Operator;
import w.whateva.life2.service.artifact.util.bucket.BucketDistributor;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ArtifactUtility {

    public <ItemType> List<List<ItemType>> putInBuckets(List<ItemType> items,
                                                        Operator<ItemType, LocalDateTime> operator,
                                                        int numBuckets,
                                                        LocalDateTime min,
                                                        LocalDateTime max) {

        return new BucketDistributor<>(items, operator, numBuckets, min, max).getBuckets();
    }
}
