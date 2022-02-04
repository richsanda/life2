package w.whateva.life2.data.pin;

import w.whateva.life2.api.artifact.dto.ApiArtifact;
import w.whateva.life2.api.artifact.dto.ApiArtifactCount;
import w.whateva.life2.api.artifact.dto.ApiArtifactSearchSpec;
import w.whateva.life2.data.pin.domain.Pin;
import w.whateva.life2.data.pin.domain.PinMonthYearCount;
import w.whateva.life2.data.pin.repository.PinDao;
import w.whateva.life2.integration.api.ArtifactProvider;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class PinProvider implements ArtifactProvider {

    private final PinDao pinDao;

    public PinProvider(PinDao pinDao) {

        this.pinDao = pinDao;
    }

    @Override
    public ApiArtifact read(String owner, String trove, String key) {
        return null;
    }

    @Override
    public List<ApiArtifact> search(LocalDate after, LocalDate before, Set<String> who, Set<String> from, Set<String> to) {
        return null;
    }

    @Override
    public List<ApiArtifact> search(ApiArtifactSearchSpec searchSpec) {

        return pinDao.search(null, searchSpec.getTroves(), searchSpec.getAfter().atStartOfDay(ZoneId.of("UTC")), searchSpec.getBefore().plusDays(1).atStartOfDay(ZoneId.of("UTC")))
                .stream()
                .map(PinProvider::toDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<ApiArtifactCount> count(LocalDate after, LocalDate before, Set<String> who, Set<String> troves) {
        return pinDao.getPinMonthYearCounts(after.atStartOfDay(), before.atStartOfDay(), who, troves).stream()
                .map(PinProvider::toDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<ApiArtifactCount> count(ApiArtifactSearchSpec searchSpec) {
        return count(searchSpec.getAfter(), searchSpec.getBefore(), searchSpec.getWho(), searchSpec.getTroves());
    }

    private static ApiArtifact toDto(Pin pin) {
        ApiArtifact result = new ApiArtifact();
        result.setWhen(null != pin.getWhen() ? pin.getWhen().toLocalDateTime() : null);
        result.setWhen2(null != pin.getWhen2() ? pin.getWhen2().toLocalDateTime() : null);
        result.setTrove(pin.getTrove());
        result.setOwner(pin.getOwner());
        result.setTitle(pin.getTitle());
        result.setRelativeKeyIndex(0);
        return result;
    }

    private static ApiArtifactCount toDto(PinMonthYearCount pinMonthYearCount) {
        ApiArtifactCount result = new ApiArtifactCount();
        result.setYear(pinMonthYearCount.getYear());
        result.setMonth(pinMonthYearCount.getMonth());
        result.setCount(pinMonthYearCount.getCount());
        return result;
    }
}

