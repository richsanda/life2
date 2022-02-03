package w.whateva.life2.data.pin.repository;

import w.whateva.life2.data.pin.domain.Pin;
import w.whateva.life2.data.pin.domain.PinMonthYearCount;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

public interface PinDao {

    List<String> listTroves();

    Pin update(Pin pin);

    List<PinMonthYearCount> getPinMonthYearCounts(Set<String> who, Set<String> from, Set<String> to, LocalDateTime after, LocalDateTime before);

    List<Pin> search(String user, Set<String> troves, ZonedDateTime after, ZonedDateTime before);
}
