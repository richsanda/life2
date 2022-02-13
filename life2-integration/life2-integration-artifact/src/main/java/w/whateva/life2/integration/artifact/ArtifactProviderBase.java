package w.whateva.life2.integration.artifact;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import w.whateva.life2.api.artifact.dto.ApiArtifact;
import w.whateva.life2.api.artifact.dto.ApiArtifactCount;
import w.whateva.life2.api.artifact.dto.ApiArtifactSearchSpec;
import w.whateva.life2.data.note.NoteDao;
import w.whateva.life2.data.note.domain.Note;
import w.whateva.life2.data.pin.domain.Pin;
import w.whateva.life2.data.pin.repository.PinDao;
import w.whateva.life2.integration.api.ArtifactProvider;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public abstract class ArtifactProviderBase<ItemType> implements ArtifactProvider {

    private final NoteDao noteDao;
    private final PinDao pinDao;

    public ArtifactProviderBase(NoteDao noteDao, PinDao pinDao) {
        this.noteDao = noteDao;
        this.pinDao = pinDao;
    }

    protected abstract ItemType read(String owner, String trove, String key);

    protected abstract ApiArtifact toDto(ItemType item, Note note, RelativesAndIndex relativesAndIndex);

    protected abstract String getPinType();

    protected abstract List<ItemType> allItemsByOwnerAndTrove(String owner, String trove);

    protected abstract String getKey(ItemType item);

    protected abstract String getTrove(ItemType item);

    protected abstract List<Pin> toIndexPins(ItemType item);

    @Override
    public final ApiArtifact read(String owner, String trove, String key, Boolean relatives) {

        ItemType item = read(owner, trove, key);

        if (null == item) return null;

        Note note = noteDao.findByTroveAndKey(trove, key);
        RelativesAndIndex relativesAndIndex = relatives ? relativesAndIndex(item) : defaultRelativesAndIndex(item);

        ApiArtifact result = toDto(item, note, relativesAndIndex);
        result.setOwner(owner);
        result.setTrove(trove);

        if (null != note) {
            result.setDescription(note.getText());
            result.setNotes(note.getNotes());
        }

        return result;
    }

    @Override
    public final List<ApiArtifact> search(ApiArtifactSearchSpec searchSpec) {

        return search(
                searchSpec.getAfter(),
                searchSpec.getBefore(),
                processPersonKeys(searchSpec.getWho()),
                searchSpec.getTroves(),
                processPersonKeys(searchSpec.getFrom()),
                processPersonKeys(searchSpec.getTo()),
                searchSpec.getText());
    }

    @Override
    public final List<ApiArtifactCount> count(ApiArtifactSearchSpec searchSpec) {

        return count(
                searchSpec.getAfter(),
                searchSpec.getBefore(),
                processPersonKeys(searchSpec.getWho()),
                processPersonKeys(searchSpec.getTroves()),
                searchSpec.getText());
    }

    @Override
    public Integer index(String owner, String trove) {
        return (int)allItemsByOwnerAndTrove(owner, trove)
                .stream()
                .map(this::index)
                .count();
    }

    protected final ItemType index(ItemType item) {
        pinDao.index(getPinType(), getTrove(item), getKey(item), toIndexPins(item));
        return item;
    }

    private Set<String> processPersonKeys(Set<String> keys) {
        if (CollectionUtils.isEmpty(keys)) return null;
        return keys.stream().map(String::toLowerCase).collect(Collectors.toSet());
    }

    protected RelativesAndIndex relativesAndIndex(ItemType item) {
        return defaultRelativesAndIndex(item);
    }

    protected RelativesAndIndex defaultRelativesAndIndex(ItemType item) {
        return RelativesAndIndex.builder()
                .relatives(Collections.singletonList(getFullKey(item)))
                .index(0)
                .build();
    }

    protected String getFullKey(ItemType item) {
        return composeFullKey(getTrove(item), getKey(item));
    }

    protected String composeFullKey(String trove, String key) {
        return trove + "/" + key;
    }

    protected Note findNoteByTroveAndKey(String trove, String key) {
        return noteDao.findByTroveAndKey(trove, key);
    }

    @Builder
    @Getter
    protected static class RelativesAndIndex {
        private final List<String> relatives;
        private final int index;
    }
}
