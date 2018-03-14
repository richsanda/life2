package w.whateva.service.life2.service.util;

import org.junit.Test;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ShredUtilityTest {

    @Test
    public void putIntoBuckets() {

        int count = 12704;

        int randMin = 1;
        int randMax = 10000;

        int min = 8000;
        int max = 9000;

        int numBuckets = 100;

        // invent a sorted list of something
        List<Integer> sorted = IntStream.range(0, count)
                .map(i -> ThreadLocalRandom.current().nextInt(randMin, randMax + 1))
                .boxed()
                .sorted()
                .collect(Collectors.toList());

        // create some number empty buckets
        List<List<Integer>> buckets = IntStream.range(0, numBuckets)
                .boxed()
                .map(i -> new ArrayList<Integer>())
                .collect(Collectors.toList());

        // figure out the max value for each bucket
        int[] bucketMaxes = IntStream.range(0, numBuckets)
                .map(i -> min + (max - min) * (i + 1) / numBuckets)
                .toArray();

        // filter sorted to fit between min and max, just in case
        sorted = sorted
                .stream()
                .filter(i -> i >= min && i <= max)
                .collect(Collectors.toList());

        // do the recursive placement into buckets
        addToBuckets(bucketMaxes, buckets, sorted);

        // print the output
        buckets.forEach(b -> System.out.println("bucket of size " + b.size()));
    }


    private int whichBucket(int[] maxes, int val) {
        int result = 0;
        while (val > maxes[result] && result < maxes.length - 1) {
            result++;
        }
        return result;
    }

    private void addToBuckets(int[] maxes, List<List<Integer>> buckets, List<Integer> sorted) {

        if (CollectionUtils.isEmpty(sorted)) return;

        int size = sorted.size();
        int lowBucket = whichBucket(maxes, sorted.get(0));
        int highBucket = whichBucket(maxes, sorted.get(size - 1));
        if (lowBucket == highBucket) {
            buckets.get(lowBucket).addAll(sorted);
        } else {
            int mid = size / 2;
            addToBuckets(maxes, buckets, sorted.subList(0, mid));
            addToBuckets(maxes, buckets, sorted.subList(mid, size));
        }
    }
}
