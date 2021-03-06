package w.whateva.life2.service.artifact.util.bucket;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BucketDistributorTest {

    private transient Logger log = LoggerFactory.getLogger(BucketDistributorTest.class);

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

        log.info("bucket size is: " + w.getBuckets().size());
    }

    protected class Thing extends AbstractLocalDateTimeOperator<Thing> {

        private LocalDateTime dt;

        protected Thing(LocalDateTime dt) {
            this.dt = dt;
        }

            @Override
            public LocalDateTime apply(Thing thing) {
                return thing.getLocalDateTime();
            }

        public LocalDateTime getLocalDateTime() {
            return dt;
        }
    }
}
