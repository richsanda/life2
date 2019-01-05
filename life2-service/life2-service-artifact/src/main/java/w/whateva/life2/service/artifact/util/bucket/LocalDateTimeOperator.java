package w.whateva.life2.service.artifact.util.bucket;

import java.time.LocalDateTime;

public class LocalDateTimeOperator extends AbstractLocalDateTimeOperator<LocalDateTime> {

    @Override
    public LocalDateTime apply(LocalDateTime localDateTime) {
        return localDateTime;
    }
}
