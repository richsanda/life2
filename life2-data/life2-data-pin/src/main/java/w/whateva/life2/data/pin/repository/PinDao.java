package w.whateva.life2.data.pin.repository;

import w.whateva.life2.data.pin.domain.Pin;
import w.whateva.life2.data.pin.domain.PinMonthYearCount;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Set;

public interface PinDao {

    List<String> listTroves();

    List<String> listTags();

    void index(String type, String trove, String key, List<Pin> pins);

    List<PinMonthYearCount> getPinMonthYearCounts(LocalDateTime after, LocalDateTime before, Set<String> who, Set<String> troves, String searchText);

    List<Pin> search(ZonedDateTime after, ZonedDateTime before, Set<String> who, Set<String> troves, String searchText);
}
