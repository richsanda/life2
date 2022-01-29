package w.whateva.life2.service.note.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.support.GenericWebApplicationContext;
import w.whateva.life2.api.note.NoteOperations;
import w.whateva.life2.api.note.dto.ApiNote;
import w.whateva.life2.data.note.NoteDao;
import w.whateva.life2.data.note.domain.Note;
import w.whateva.life2.data.note.repository.NoteRepository;
import w.whateva.life2.integration.api.ArtifactProvider;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static w.whateva.life2.service.note.impl.NoteUtil.enhanceNote;

@Service
public class NoteServiceImpl implements NoteOperations {

    private final GenericWebApplicationContext context;

    private final NoteRepository noteRepository;
    private final NoteDao noteDao;
    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    public NoteServiceImpl(GenericWebApplicationContext context, NoteRepository noteRepository, NoteDao NoteDao) {
        this.context = context;
        this.noteRepository = noteRepository;
        this.noteDao = NoteDao;
        context.registerBean("whatever_dude",
                ArtifactProvider.class,
                () -> new NoteProvider(this, noteDao));
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

    private Note update(Note note) {
        noteRepository.save(note);
        return note;
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

        return noteRepository.findAll().stream()
                // .map(NoteUtil::enhanceNote)
                .filter(n -> n.getData().containsKey("when") && n.getData().containsKey("type") && null != n.getData().get("type"))
                .sorted(Comparator.comparing((n) -> n.getData().get("when").toString()))
                .map(n -> "" + n.getData().get("when") + "." + n.getData().getOrDefault("where", "") + " (" + n.getData().get("people") + ") -- " + n.getData().get("type") + " -- " + n.getTrove())
                .collect(Collectors.joining("\n"));
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
        BeanUtils.copyProperties(enhanceNote(note), result);
        return result;
    }

    private static String composeKey(String trove, String key) {
        return String.format("%s/%s.jpg", trove, key).toLowerCase();
    }

}
