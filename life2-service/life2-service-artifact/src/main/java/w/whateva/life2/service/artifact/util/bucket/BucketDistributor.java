package w.whateva.life2.service.artifact.util.bucket;

import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BucketDistributor<DomainType, RangeType extends Comparable<? super RangeType>> {

    private final List<DomainType> shreds;
    private final Operator<DomainType, RangeType> operator;
    private final int numBuckets;
    private final RangeType min;
    private final RangeType max;

    private final List<List<DomainType>> buckets;

    public BucketDistributor(List<DomainType> shreds,
                             Operator<DomainType, RangeType> operator,
                             int numBuckets,
                             RangeType min,
                             RangeType max) {

        this.shreds = shreds
                .stream()
                .filter(i -> {
                    RangeType r = operator.apply(i);
                    return operator.compare(r, min) >= 0 && operator.compare(r, max) < 0;
                })
                .sorted(Comparator.comparing(operator::apply))
                .collect(Collectors.toList());

        this.operator = operator;
        this.numBuckets = numBuckets;
        this.min = min;
        this.max = max;

        this.buckets = fillBuckets();
    }

    public List<List<DomainType>> getBuckets() {
        return buckets;
    }

    private List<List<DomainType>> fillBuckets() {

        // create some number empty buckets
        List<List<DomainType>> buckets = IntStream.range(0, numBuckets)
                .boxed()
                .map(i -> new ArrayList<DomainType>())
                .collect(Collectors.toList());

        // figure out the max value for each bucket
        List<RangeType> bucketMaxes = IntStream.range(0, numBuckets)
                .boxed()
                .map(i -> operator.getBucketMax(min, max, i, numBuckets))
                .collect(Collectors.toList());

        // do the recursive placement into buckets
        addToBuckets(bucketMaxes, buckets, shreds);

        return buckets;
    }

    private int whichBucket(List<RangeType> maxes, DomainType shred) {
        int result = 0;
        while (operator.compare(operator.apply(shred), maxes.get(result)) > 0 && result < maxes.size() - 1) {
            result++;
        }
        return result;
    }

    private void addToBuckets(List<RangeType> maxes, List<List<DomainType>> buckets, List<DomainType> shreds) {

        if (CollectionUtils.isEmpty(shreds)) return;

        int size = shreds.size();
        int lowBucket = whichBucket(maxes, shreds.get(0));
        int highBucket = whichBucket(maxes, shreds.get(size - 1));
        if (lowBucket == highBucket) {
            buckets.get(lowBucket).addAll(shreds);
        } else {
            int mid = size / 2;
            addToBuckets(maxes, buckets, shreds.subList(0, mid));
            addToBuckets(maxes, buckets, shreds.subList(mid, size));
        }
    }
}
