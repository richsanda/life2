package w.whateva.service.life2.service.util;

import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ShredBucketer<ItemType> {

    private final List<ItemType> shreds;
    private final Function<ItemType, LocalDateTime> function;
    private final int numBuckets;
    private final LocalDateTime min;
    private final LocalDateTime max;

    private final List<List<ItemType>> buckets;

    public ShredBucketer(List<ItemType> shreds,
                         Function<ItemType, LocalDateTime> function,
                         int numBuckets,
                         LocalDateTime min,
                         LocalDateTime max) {

        this.shreds = shreds
                .stream()
                .filter(i -> {
                    LocalDateTime d = function.apply(i);
                    return d.isAfter(min) && d.isBefore(max);
                })
                .sorted(Comparator.comparing(function))
                .collect(Collectors.toList());

        this.function = function;
        this.numBuckets = numBuckets;
        this.min = min;
        this.max = max;

        this.buckets = fillBuckets();
    }

    public List<List<ItemType>> getBuckets() {
        return buckets;
    }

    private List<List<ItemType>> fillBuckets() {

        // create some number empty buckets
        List<List<ItemType>> buckets = IntStream.range(0, numBuckets)
                .boxed()
                .map(i -> new ArrayList<ItemType>())
                .collect(Collectors.toList());

        // figure out the max value for each bucket
        LocalDateTime[] bucketMaxes = (LocalDateTime[]) IntStream.range(0, numBuckets)
                .boxed()
                .map(this::getMax)
                .toArray();

        // do the recursive placement into buckets
        addToBuckets(bucketMaxes, buckets, shreds);

        return buckets;
    }

    private LocalDateTime getMax(int i) {
        return min.plus(Duration.between(max, min).dividedBy((i + 1) / numBuckets));
    }

    private int whichBucket(LocalDateTime[] maxes, ItemType shred) {
        int result = 0;
        while (function.apply(shred).isBefore(maxes[result]) && result < maxes.length - 1) {
            result++;
        }
        return result;
    }

    private void addToBuckets(LocalDateTime[] maxes, List<List<ItemType>> buckets, List<ItemType> shreds) {

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
