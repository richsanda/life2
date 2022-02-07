package w.whateva.life2.service.neat.impl;

import w.whateva.life2.api.artifact.dto.ApiArtifact;
import w.whateva.life2.api.artifact.dto.ApiArtifactCount;
import w.whateva.life2.api.artifact.dto.ApiArtifactSearchSpec;
import w.whateva.life2.data.neat.NeatDao;
import w.whateva.life2.data.neat.domain.NeatFile;
import w.whateva.life2.data.neat.repository.NeatFileRepository;
import w.whateva.life2.data.note.NoteDao;
import w.whateva.life2.data.note.domain.Note;
import w.whateva.life2.data.note.domain.NoteMonthYearCount;
import w.whateva.life2.data.pin.repository.PinDao;
import w.whateva.life2.integration.api.ArtifactProvider;
import w.whateva.life2.service.note.impl.NoteServiceImpl;
import w.whateva.life2.service.note.impl.NoteUtil;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static w.whateva.life2.service.note.impl.NoteUtil.when;

public class NeatProvider implements ArtifactProvider {

    private final NeatFileRepository neatFileRepository;
    private final NeatDao neatDao;
    private final NoteDao noteDao;
    private final PinDao pinDao;

    NeatProvider(NeatFileRepository neatFileRepository, NeatDao neatDao, NoteDao noteDao, PinDao pinDao) {
        this.neatFileRepository = neatFileRepository;
        this.neatDao = neatDao;
        this.noteDao = noteDao;
        this.pinDao = pinDao;
    }

    @Override
    public ApiArtifact read(String owner, String trove, String key) {

        NeatFile neatFile = neatFileRepository.findById(trove + "/" + key).orElse(null);
        if (null == neatFile) return null;
        List<String> relatives = neatDao.findByFolderSorted(trove).stream()
                .map(NeatFile::getId)
                .collect(Collectors.toUnmodifiableList());
        int index = relatives.indexOf(neatFile.getId());
        Note note = noteDao.findByTroveAndKey(trove, key);
        return toDto(neatFile, note, relatives, index);
    }

    @Override
    public List<ApiArtifact> search(LocalDate after, LocalDate before, Set<String> who, Set<String> troves, Set<String> from, Set<String> to, String text) {
        return null; // uses pin index
    }

    @Override
    public List<ApiArtifact> search(ApiArtifactSearchSpec searchSpec) {
        return null; // uses pin index
    }

    @Override
    public List<ApiArtifactCount> count(LocalDate after, LocalDate before, Set<String> who, Set<String> troves, String text) {
        return null; // uses pin index
    }

    @Override
    public List<ApiArtifactCount> count(ApiArtifactSearchSpec searchSpec) {
        return null; // uses pin index
    }

    @Override
    public Integer index(String owner, String trove) {

        List<NeatFile> neatFiles = neatFileRepository.findByFolderOrderByFolderAsc(trove)
                .stream()
                .map(this::indexWithNote)
                .collect(Collectors.toUnmodifiableList());

        return neatFiles.size();
    }

    private NeatFile indexWithNote(NeatFile neatFile) {
        String[] parts = neatFile.getId().split("/");
        Note note = noteDao.findByTroveAndKey(parts[0], parts[1]);
        index(neatFile, note);
        return neatFile;
    }

    // index by note for now...
    private Note index(NeatFile neatFile, Note note) {
        pinDao.update(NoteUtil.index(note));
        return note;
    }

    public static ApiArtifact toDto(NeatFile neatFile, Note note, List<String> relatives, int index) {

        ZonedDateTime when = when(note);

        ApiArtifact result = new ApiArtifact();
        result.setTypes(new HashSet<>());
        result.getTypes().add("note");
        result.setWhen(null != when ? when.toLocalDateTime() : null);
        result.setTitle(note.getData().containsKey("type") ? note.getData().get("type").toString() : null);
        result.setTrove(note.getTrove());
        result.setImage(imageLocation(neatFile));
        result.setKey(neatFile.getId());
        result.setDescription(note.getText());
        result.setData(note.getData());
        result.getData().put("neat", neatFile);
        result.setRelativeKeys(relatives);
        result.setRelativeKeyIndex(index);
        return result;
    }

    public static ApiArtifactCount toDto(NoteMonthYearCount count) {
        ApiArtifactCount result = new ApiArtifactCount();
        result.setCount(count.getCount());
        result.setMonth(count.getMonth());
        result.setYear(count.getYear());
        return result;
    }

    private static String imageLocation(NeatFile neatFile) {
        return "w/neat/" + neatFile.getId();
    }

}
