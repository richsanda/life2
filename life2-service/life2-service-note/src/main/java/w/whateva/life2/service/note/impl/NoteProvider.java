package w.whateva.life2.service.note.impl;

import w.whateva.life2.api.artifact.dto.ApiArtifact;
import w.whateva.life2.api.artifact.dto.ApiArtifactCount;
import w.whateva.life2.api.artifact.dto.ApiArtifactSearchSpec;
import w.whateva.life2.data.note.NoteDao;
import w.whateva.life2.data.note.domain.Note;
import w.whateva.life2.integration.api.ArtifactProvider;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static w.whateva.life2.service.note.impl.NoteUtil.toDto;

public class NoteProvider implements ArtifactProvider {

    private final NoteServiceImpl noteService;
    private final NoteDao noteDao;

    NoteProvider(NoteServiceImpl noteService, NoteDao noteDao) {
        this.noteService = noteService;
        this.noteDao = noteDao;
    }

    @Override
    public ApiArtifact read(String owner, String trove, String key) {
        Note note = noteDao.findByTroveAndKey(trove, key);
        if (null == note) return null;
        List<String> relatives = noteDao.findByTroveSorted(trove).stream()
                .map(Note::getId)
                .filter(id -> id.endsWith(".jpg"))
                .collect(Collectors.toUnmodifiableList());
        int index = relatives.indexOf(note.getId());
        return toDto(note, relatives, index);
    }

    @Override
    public List<ApiArtifact> search(LocalDate after, LocalDate before, Set<String> who, Set<String> from, Set<String> to) {
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
    public List<ApiArtifactCount> count(LocalDate after, LocalDate before, Set<String> who, Set<String> from, Set<String> to) {
        return noteDao.getNoteMonthYearCounts(who, from, to, after.atStartOfDay(), before.atStartOfDay()).stream()
                .map(NoteUtil::toDto)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<ApiArtifactCount> count(ApiArtifactSearchSpec searchSpec) {
        return count(searchSpec.getAfter(), searchSpec.getBefore(), null, null, null);
    }
}
