package w.whateva.life2.service.artifact.util.bucket;

import org.junit.Test;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BucketDistributorTest {

    @Test
    public void putIntoBuckets() {

        BucketDistributor<Thing, LocalDateTime> w = new BucketDistributor<Thing, LocalDateTime>(
                IntStream.range(2018, 2080)
                        .boxed()
                        .map(i -> LocalDateTime.of(i, 11, 11, 11, 11))
                        .map(Thing::new)
                        .collect(Collectors.toList()),
                new Thing(LocalDateTime.now()),
                20,
                LocalDateTime.now(),
                LocalDateTime.now().plusYears(20)
        );

        System.out.println(w.getBuckets().size());
    }

    protected class Thing extends AbstractLocalDateTimeOperator<Thing> {

        private LocalDateTime dt;

        protected Thing(LocalDateTime dt) {
            this.dt = dt;
        }

            @Override
            public LocalDateTime apply(Thing shred) {
                return shred.getLocalDateTime();
            }

        public LocalDateTime getLocalDateTime() {
            return dt;
        }
    }
}
