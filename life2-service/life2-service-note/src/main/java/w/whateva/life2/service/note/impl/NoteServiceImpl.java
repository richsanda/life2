package w.whateva.life2.service.note.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import w.whateva.life2.api.note.NoteOperations;
import w.whateva.life2.api.note.dto.ApiNote;
import w.whateva.life2.data.note.NoteDao;
import w.whateva.life2.data.note.domain.Note;
import w.whateva.life2.data.note.repository.NoteRepository;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NoteServiceImpl implements NoteOperations {

    private final NoteRepository noteRepository;
    private final NoteDao noteDao;

    @Autowired
    public NoteServiceImpl(NoteRepository noteRepository, NoteDao NoteDao) {
        this.noteRepository = noteRepository;
        this.noteDao = NoteDao;
    }

    @Override
    public ApiNote update(String trove, String key, ApiNote apiNote) {

        apiNote.setTrove(trove);
        apiNote.setKey(key);

        if (null == apiNote.getKey()) return null;

        Note note = new Note();
        note.setId(composeKey(trove, key));
        BeanUtils.copyProperties(apiNote, note);
        noteRepository.save(note);

        return toApi(note);
    }

    @Override
    public List<ApiNote> update(String trove, List<ApiNote> notes) {
        return notes.stream()
                .map(note -> update(note.getTrove(), note.getKey(), note))
                .collect(Collectors.toList());
    }

    @Override
    public ApiNote read(String folder, String filename) {
        Note Note = noteRepository.findById(composeKey(folder, filename)).orElse(null);
        if (null == Note) return null;
        ApiNote apiNote = new ApiNote();
        BeanUtils.copyProperties(Note, apiNote);
        return apiNote;
    }

    @Override
    public List<ApiNote> readTrove(String folder) {
        Collection<Note> Notes = noteDao.findByTroveSorted(folder);
        if (CollectionUtils.isEmpty(Notes)) return Collections.emptyList();
        return Notes.stream().map(NoteServiceImpl::toApi).collect(Collectors.toList());
    }

    @Override
    public List<String> listTroves() {
        return noteDao.listTroves();
    }

    public List<String> allNoteKeys() {
        return noteRepository.findAll().stream().map(Note::getId).collect(Collectors.toList());
    }

    private static ApiNote toApi(Note Note) {
        if (null == Note) return null;
        ApiNote result = new ApiNote();
        BeanUtils.copyProperties(Note, result);
        return result;
    }

    private static String composeKey(String trove, String key) {
        return String.format("%s/%s.jpg", trove, key).toLowerCase();
    }
}
