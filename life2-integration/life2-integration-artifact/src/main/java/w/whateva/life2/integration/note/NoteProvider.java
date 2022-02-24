package w.whateva.life2.integration.note;

import w.whateva.life2.api.artifact.dto.ApiArtifact;
import w.whateva.life2.api.artifact.dto.ApiArtifactCount;
import w.whateva.life2.data.note.NoteDao;
import w.whateva.life2.data.note.domain.Note;
import w.whateva.life2.data.note.repository.NoteRepository;
import w.whateva.life2.data.pin.domain.Pin;
import w.whateva.life2.data.pin.repository.PinDao;
import w.whateva.life2.integration.artifact.ArtifactProviderBase;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static w.whateva.life2.integration.note.NoteUtil.NOTE_PIN_TYPE;

public class NoteProvider extends ArtifactProviderBase<Note> {

    private final NoteRepository noteRepository;
    private final NoteDao noteDao;

    public NoteProvider(NoteRepository noteRepository, NoteDao noteDao, PinDao pinDao) {
        super(noteRepository, pinDao);
        this.noteRepository = noteRepository;
        this.noteDao = noteDao;
    }

    @Override
    public Note read(String owner, String trove, String key) {
        return noteRepository.findByTroveAndKey(trove, key);
    }

    @Override
    protected ApiArtifact toDto(Note note, Note ignore, RelativesAndIndex relativesAndIndex) {
        return NoteUtil.toDto(note);
    }

    @Override
    protected String getPinType() {
        return NOTE_PIN_TYPE;
    }

    @Override
    protected List<Note> allItemsByOwnerAndTrove(String owner, String trove) {
        return noteDao.findByTroveSorted(trove);
    }

    @Override
    protected String getKey(Note note) {
        return note.getKey();
    }

    @Override
    protected String getTrove(Note note) {
        return note.getTrove();
    }

    @Override
    protected List<Pin> toIndexPins(Note note) {
        return NoteUtil.toIndexPins(note);
    }

    @Override
    public List<ApiArtifact> search(LocalDate after, LocalDate before, Set<String> who, Set<String> troves, Set<String> from, Set<String> to, String text, String source) {
        return null;
    }

    @Override
    public List<ApiArtifactCount> count(LocalDate after, LocalDate before, Set<String> who, Set<String> troves, String text, String source) {
        return noteDao.getNoteMonthYearCounts(after.atStartOfDay(), before.atStartOfDay(), who, troves).stream()
                .map(NoteUtil::toDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Integer index(String owner, String trove) {

        List<Note> notes = noteRepository.findAllByTrove(trove);
        notes.forEach(this::index);

        return notes.size();
    }

    @Override
    protected Note findNote(Note item) {
        return null; // no need to find a note for a standalone note...
    }
}
