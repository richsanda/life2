package w.whateva.life2.service.artifact.util.bucket;

import java.time.Duration;
import java.time.LocalDateTime;

public abstract class AbstractLocalDateTimeOperator<DomainType> implements Operator<DomainType, LocalDateTime> {

    @Override
    public abstract LocalDateTime apply(DomainType shred);

    public LocalDateTime getBucketMax(LocalDateTime min, LocalDateTime max, int i, int numBuckets) {
        return min.plus(Duration.between(min, max).dividedBy(numBuckets).multipliedBy(i + 1));
    }

    @Override
    public int compare(LocalDateTime one, LocalDateTime two) {
        return one.isAfter(two) ? 1 : one.isEqual(two) ? 0 : -1;
    }
}
