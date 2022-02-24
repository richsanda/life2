package w.whateva.life2.integration.neat;

import w.whateva.life2.api.artifact.dto.ApiArtifact;
import w.whateva.life2.api.artifact.dto.ApiArtifactCount;
import w.whateva.life2.data.neat.NeatDao;
import w.whateva.life2.data.neat.domain.NeatFile;
import w.whateva.life2.data.neat.repository.NeatFileRepository;
import w.whateva.life2.data.note.domain.Note;
import w.whateva.life2.data.note.repository.NoteRepository;
import w.whateva.life2.data.pin.domain.Pin;
import w.whateva.life2.data.pin.repository.PinDao;
import w.whateva.life2.integration.artifact.ArtifactProviderBase;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static w.whateva.life2.integration.note.NoteUtil.*;

public class NeatProvider extends ArtifactProviderBase<NeatFile> {

    private final static String NEAT_PIN_TYPE = "neat";

    private final NeatFileRepository neatFileRepository;
    private final NeatDao neatDao;

    public NeatProvider(NeatFileRepository neatFileRepository, NeatDao neatDao, NoteRepository noteRepository, PinDao pinDao) {
        super(noteRepository, pinDao);
        this.neatFileRepository = neatFileRepository;
        this.neatDao = neatDao;
    }

    @Override
    public NeatFile read(String owner, String trove, String key) {
        return neatFileRepository.findById(composeFullKey(trove, key)).orElse(null);
    }

    @Override
    protected String getPinType() {
        return NEAT_PIN_TYPE;
    }

    @Override
    protected List<NeatFile> allItemsByOwnerAndTrove(String owner, String trove) {
        return neatDao.findByFolderSorted(trove);
    }

    @Override
    protected String getKey(NeatFile neatFile) {
        return neatFile.getId().split("/")[1];
    }

    @Override
    protected String getTrove(NeatFile neatFile) {
        return neatFile.getFolder();
    }

    @Override
    protected List<Pin> toIndexPins(NeatFile neatFile) {
        return Collections.emptyList(); // no indexing for now... corresponding notes cover it
    }

    @Override
    public RelativesAndIndex relativesAndIndex(NeatFile neatFile) {
        List<String> relatives = neatDao.findByFolderSorted(neatFile.getFolder()).stream()
                .map(NeatFile::getId)
                .collect(Collectors.toUnmodifiableList());
        int index = relatives.indexOf(neatFile.getId());
        return RelativesAndIndex.builder()
                .relatives(relatives)
                .index(index)
                .build();
    }

    @Override
    public List<ApiArtifact> search(LocalDate after, LocalDate before, Set<String> who, Set<String> troves, Set<String> from, Set<String> to, String text, String source) {
        return null; // uses pin index
    }

    @Override
    public List<ApiArtifactCount> count(LocalDate after, LocalDate before, Set<String> who, Set<String> troves, String text, String source) {
        return null; // uses pin index
    }

    @Override
    public Integer index(String owner, String trove) {

        List<NeatFile> neatFiles = neatFileRepository.findByFolderOrderByFolderAsc(trove)
                .stream()
                .map(this::index)
                .collect(Collectors.toUnmodifiableList());

        return neatFiles.size();
    }

    @Override
    public ApiArtifact toDto(NeatFile neatFile, Note note, RelativesAndIndex relativesAndIndex) {

        Map<String, Object> data = fields(note.getText());

        ZonedDateTime when = when(data);
        String title = title(data);

        ApiArtifact result = new ApiArtifact();
        result.setTypes(new HashSet<>());
        result.getTypes().add(NEAT_PIN_TYPE);
        result.setWhen(null != when ? when.toLocalDateTime() : null);
        result.setTitle(title);
        result.setTrove(note.getTrove());
        result.setImage(imageLocation(neatFile));
        result.setKey(neatFile.getId());
        result.setDescription(note.getText());
        result.setData(data);
        result.getData().put("neat", neatFile);
        result.setNotes(note.getNotes());
        result.setRelativeKeys(relativesAndIndex.getRelatives());
        result.setRelativeKeyIndex(relativesAndIndex.getIndex());
        return result;
    }

    private static String imageLocation(NeatFile neatFile) {
        return "w/neat/" + neatFile.getId();
    }

}
