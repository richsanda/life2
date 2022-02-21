package w.whateva.life2.data.pin;

import org.springframework.util.CollectionUtils;
import w.whateva.life2.api.artifact.dto.ApiArtifact;
import w.whateva.life2.api.artifact.dto.ApiArtifactCount;
import w.whateva.life2.api.artifact.dto.ApiArtifactSearchSpec;
import w.whateva.life2.api.person.PersonService;
import w.whateva.life2.data.pin.domain.Pin;
import w.whateva.life2.data.pin.domain.PinMonthYearCount;
import w.whateva.life2.data.pin.repository.PinDao;
import w.whateva.life2.integration.api.ArtifactProvider;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class PinProvider implements ArtifactProvider {

    private final PinDao pinDao;
    private final PersonService personService;

    public PinProvider(PinDao pinDao, PersonService personService) {

        this.pinDao = pinDao;
        this.personService = personService;
    }

    @Override
    public ApiArtifact read(String owner, String trove, String key, Boolean relatives) {
        return null;
    }

    @Override
    public List<ApiArtifact> search(LocalDate after, LocalDate before, Set<String> who, Set<String> troves, Set<String> from, Set<String> to, String text) {

        return pinDao.search(
                        null != after ? after.atStartOfDay(ZoneId.of("UTC")) : null,
                        null != before ? before.plusDays(1).atStartOfDay(ZoneId.of("UTC")) : null,
                        who,
                        troves,
                        text)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<ApiArtifact> search(ApiArtifactSearchSpec searchSpec) {

        return search(
                searchSpec.getAfter(),
                searchSpec.getBefore(),
                searchSpec.getWho(),
                searchSpec.getTroves(),
                Collections.emptySet(),
                Collections.emptySet(),
                null);
    }

    @Override
    public List<ApiArtifactCount> count(LocalDate after, LocalDate before, Set<String> who, Set<String> troves, String text) {
        return pinDao.getPinMonthYearCounts(
                        after.atStartOfDay(),
                        before.atStartOfDay(),
                        who,
                        troves,
                        text).stream()
                .map(PinProvider::toDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<ApiArtifactCount> count(ApiArtifactSearchSpec searchSpec) {
        return count(
                searchSpec.getAfter(),
                searchSpec.getBefore(),
                searchSpec.getWho(),
                searchSpec.getTroves(),
                searchSpec.getText());
    }

    @Override
    public Integer index(String owner, String trove) {
        return 0;
    }

    private ApiArtifact toDto(Pin pin) {

        ApiArtifact result = new ApiArtifact();
        result.setWhen(null != pin.getWhen() ? pin.getWhen().toLocalDateTime() : null);
        result.setWhen2(null != pin.getWhen2() ? pin.getWhen2().toLocalDateTime() : null);
        result.setWhenDisplay(pin.getWhenDisplay());
        result.setTrove(pin.getTrove());
        result.setKey(pin.getKey());
        result.setOwner(pin.getOwner());
        result.setTitle(title(pin));
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

    private String title(Pin pin) {

        if (CollectionUtils.isEmpty(pin.getFrom()) || CollectionUtils.isEmpty(pin.getTo())) {
            return pin.getTitle();
        }

        return pin.getFrom().stream()
                .map(personService::emailToPersonName)
                .filter(Objects::nonNull)
                .collect(Collectors.joining(", ")) +
                " -> " +
                pin.getTo().stream()
                        .map(personService::emailToPersonName)
                        .filter(Objects::nonNull)
                        .collect(Collectors.joining(", "))
                + " [" + pin.getTitle() + "]";

    }
}

