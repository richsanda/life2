package w.whateva.life2.service.note.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.support.GenericWebApplicationContext;
import w.whateva.life2.api.note.NoteOperations;
import w.whateva.life2.api.note.dto.ApiNote;
import w.whateva.life2.data.neat.NeatDao;
import w.whateva.life2.data.note.NoteDao;
import w.whateva.life2.data.note.domain.Note;
import w.whateva.life2.data.note.repository.NoteRepository;
import w.whateva.life2.data.pin.repository.PinDao;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static w.whateva.life2.service.note.impl.NoteUtil.fields;

@Service
public class NoteServiceImpl implements NoteOperations {

    private final NoteRepository noteRepository;
    private final NoteDao noteDao;
    private final NeatDao neatDao;
    private final PinDao pinDao;

    @Autowired
    public NoteServiceImpl(GenericWebApplicationContext context, NoteRepository noteRepository, NoteDao NoteDao, NeatDao neatDao, PinDao pinDao) {
        this.noteRepository = noteRepository;
        this.noteDao = NoteDao;
        this.neatDao = neatDao;
        this.pinDao = pinDao;
//        context.registerBean("NoteProvider",
//                ArtifactProvider.class,
//                () -> new NoteProvider(noteRepository, noteDao, this.neatDao, pinDao));
    }

    @Override
    public ApiNote update(String trove, String key, ApiNote apiNote) {

        apiNote.setTrove(trove);
        apiNote.setKey(key);

        if (null == apiNote.getKey()) return null;

        Note note = new Note();
        note.setId(composeKey(trove, key));
        BeanUtils.copyProperties(apiNote, note);
        return toApi(update(note));
    }

    private Note update(Note note) {
        noteRepository.save(note);
        index(note);
        return note;
    }

    private void reindexAllNotes() {
        noteRepository.findAll().forEach(this::index);
    }

    private void index(Note note) {
        pinDao.update(NoteUtil.index(note));
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
    public String test() {

//        Collection<Note> notes = noteRepository.findAll().stream()
//                .flatMap(n -> splitNote(n).stream())
//                .map(NoteUtil::enhanceNote)
//                .filter(n -> n.getData().containsKey("when") && n.getData().containsKey("type") && null != n.getData().get("type"))
//                .sorted(Comparator.comparing((n) -> n.getData().get("when").toString()))
//                .collect(Collectors.toUnmodifiableList());

        // noteRepository.saveAll(notes);

//        List<Pin> pins = noteRepository.findAll().stream()
//                // .map(NoteUtil::enhanceNote)
//                .filter(n -> n.getData().containsKey("when") && n.getData().containsKey("type") && null != n.getData().get("type"))
//                .sorted(Comparator.comparing((n) -> n.getData().get("when").toString()))
//                .map(NoteServiceImpl::toPin)
//                .collect(Collectors.toUnmodifiableList());
//
//        pins.forEach(pinDao::add);
//
//        return pins.stream().map(p -> p.getWhen().toString() + p.getTrove()).collect(Collectors.joining(","));

        reindexAllNotes();

        return "did it";
    }

    @Override
    public List<String> listTroves() {
        return noteDao.listTroves();
    }

    public List<String> allNoteKeys() {
        return noteRepository.findAll().stream().map(Note::getId).collect(Collectors.toList());
    }

    public static ApiNote toApi(Note note) {
        if (null == note) return null;
        ApiNote result = new ApiNote();
        BeanUtils.copyProperties(note, result);
        result.setData(fields(note.getText()));
        return result;
    }

    private static String composeKey(String trove, String key) {
        return String.format("%s/%s.jpg", trove, key).toLowerCase();
    }

}
