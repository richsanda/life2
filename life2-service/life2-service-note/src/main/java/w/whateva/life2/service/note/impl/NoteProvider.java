package w.whateva.life2.service.note.impl;

import w.whateva.life2.api.artifact.dto.ApiArtifact;
import w.whateva.life2.api.artifact.dto.ApiArtifactCount;
import w.whateva.life2.api.artifact.dto.ApiArtifactSearchSpec;
import w.whateva.life2.data.neat.NeatDao;
import w.whateva.life2.data.neat.domain.NeatFile;
import w.whateva.life2.data.note.NoteDao;
import w.whateva.life2.data.note.domain.Note;
import w.whateva.life2.data.note.repository.NoteRepository;
import w.whateva.life2.data.pin.repository.PinDao;
import w.whateva.life2.integration.api.ArtifactProvider;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static w.whateva.life2.service.note.impl.NoteUtil.toDto;

public class NoteProvider implements ArtifactProvider {

    private final NoteRepository noteRepository;
    private final NoteDao noteDao;
    private final NeatDao neatDao;
    private final PinDao pinDao;

    public NoteProvider(NoteRepository noteRepository, NoteDao noteDao, NeatDao neatDao, PinDao pinDao) {
        this.noteRepository = noteRepository;
        this.noteDao = noteDao;
        this.neatDao = neatDao;
        this.pinDao = pinDao;
    }

    @Override
    public ApiArtifact read(String owner, String trove, String key) {
        Note note = noteDao.findByTroveAndKey(trove, key);
        if (null == note) return null;
        List<String> relatives = neatDao.findByFolderSorted(trove).stream()
                .map(NeatFile::getId)
                .collect(Collectors.toUnmodifiableList());
        int index = relatives.indexOf(note.getId());
        return toDto(note, relatives, index);
    }

    @Override
    public List<ApiArtifact> search(LocalDate after, LocalDate before, Set<String> who, Set<String> troves, Set<String> from, Set<String> to, String text) {
        return null;
    }

    @Override
    public List<ApiArtifact> search(ApiArtifactSearchSpec searchSpec) {

        return noteDao.getNotes(Collections.emptySet(), Collections.emptySet(), Collections.emptySet(), searchSpec.getAfter().atStartOfDay(), searchSpec.getBefore().plusDays(1).atStartOfDay())
                .stream()
                .map(NoteUtil::toDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<ApiArtifactCount> count(LocalDate after, LocalDate before, Set<String> who, Set<String> troves, String text) {
        return noteDao.getNoteMonthYearCounts(after.atStartOfDay(), before.atStartOfDay(), who, troves).stream()
                .map(NoteUtil::toDto)
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

        List<Note> notes = noteRepository.findAllByTrove(trove)
                .stream()
                .map(this::index)
                .collect(Collectors.toUnmodifiableList());

        return notes.size();
    }

    private Note index(Note note) {
        pinDao.update(NoteUtil.index(note));
        return note;
    }
}
