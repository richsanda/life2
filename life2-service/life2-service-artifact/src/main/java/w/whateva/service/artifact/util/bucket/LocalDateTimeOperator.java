package w.whateva.service.life2.service.util.bucket;

import java.time.LocalDateTime;

public class LocalDateTimeOperator extends AbstractLocalDateTimeOperator<LocalDateTime> {

    @Override
    public LocalDateTime apply(LocalDateTime shred) {
        return shred;
    }
}
