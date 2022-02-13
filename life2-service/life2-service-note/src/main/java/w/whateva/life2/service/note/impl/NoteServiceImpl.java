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
import w.whateva.life2.data.pin.repository.PinDao;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static w.whateva.life2.service.note.impl.NoteUtil.*;

@Service
public class NoteServiceImpl implements NoteOperations {

    private final NoteRepository noteRepository;
    private final NoteDao noteDao;
    private final PinDao pinDao;

    @Autowired
    public NoteServiceImpl(NoteRepository noteRepository, NoteDao NoteDao, PinDao pinDao) {
        this.noteRepository = noteRepository;
        this.noteDao = NoteDao;
        this.pinDao = pinDao;
    }

    @Override
    public ApiNote update(String trove, String key, ApiNote apiNote) {

        apiNote.setTrove(trove);
        apiNote.setKey(key);

        if (null == apiNote.getKey()) return null;

        Note note = new Note();
        note.setId(composeKey(trove, key));
        note.setTrove(apiNote.getTrove());
        note.setText(apiNote.getText());
        note.setNotes(apiNote.getNotes());
        note.setData(apiNote.getData());
        update(note);
        return toApi(note);
    }

    private void update(Note note) {
        noteRepository.save(note);
        pinDao.index(NOTE_PIN_TYPE, note.getTrove(), noteKey(note), toIndexPins(note));
    }

    private void reindexAllNotes() {
        noteRepository.findAll().forEach(this::update);
    }

    @Override
    public List<ApiNote> update(String trove, List<ApiNote> notes) {
        return notes.stream()
                .map(note -> update(note.getTrove(), note.getKey(), note))
                .collect(Collectors.toList());
    }

    @Override
    public ApiNote read(String folder, String filename) {
        Note note = noteRepository.findById(composeKey(folder, filename)).orElse(null);
        return toApi(note);
    }

    @Override
    public List<ApiNote> readTrove(String folder) {
        Collection<Note> Notes = noteDao.findByTroveSorted(folder);
        if (CollectionUtils.isEmpty(Notes)) return Collections.emptyList();
        return Notes.stream().map(NoteServiceImpl::toApi).collect(Collectors.toList());
    }

    @Override
    public String applyToNotes() {

         reindexAllNotes();

         return ":)";
    }

    @Override
    public List<String> listTroves() {
        return noteDao.listTroves();
    }

    public static ApiNote toApi(Note note) {
        if (null == note) return null;
        ApiNote result = new ApiNote();
        BeanUtils.copyProperties(note, result);
        result.setData(note.getData());
        return result;
    }

    private static String composeKey(String trove, String key) {
        return String.format("%s/%s", trove, key).toLowerCase();
    }
}
