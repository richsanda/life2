package w.whateva.life2.service.note.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.support.GenericWebApplicationContext;
import w.whateva.life2.api.note.NoteOperations;
import w.whateva.life2.api.note.dto.ApiNote;
import w.whateva.life2.data.neat.NeatDao;
import w.whateva.life2.data.note.NoteDao;
import w.whateva.life2.data.note.domain.Note;
import w.whateva.life2.data.note.repository.NoteRepository;
import w.whateva.life2.data.pin.domain.Pin;
import w.whateva.life2.data.pin.repository.PinDao;
import w.whateva.life2.integration.api.ArtifactProvider;

import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static w.whateva.life2.service.note.impl.NoteUtil.enhanceNote;

@Service
public class NoteServiceImpl implements NoteOperations {

    private final GenericWebApplicationContext context;

    private final NoteRepository noteRepository;
    private final NoteDao noteDao;
    private final NeatDao neatDao;
    private final PinDao pinDao;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    public NoteServiceImpl(GenericWebApplicationContext context, NoteRepository noteRepository, NoteDao NoteDao, NeatDao neatDao, PinDao pinDao) {
        this.context = context;
        this.noteRepository = noteRepository;
        this.noteDao = NoteDao;
        this.neatDao = neatDao;
        this.pinDao = pinDao;
        context.registerBean("whatever_dude",
                ArtifactProvider.class,
                () -> new NoteProvider(this, noteDao, this.neatDao));
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
        pinDao.update(toPin(note));
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

        return noteRepository.findAll().stream()
                .map(n -> NoteUtil.updateNoteText(n.getText()))
                .collect(Collectors.joining("\n\n"));
    }

    private static Pin toPin(Note note) {
        Set<String> types = new HashSet<>();
        if (note.getData().containsKey("type")) types.add(note.getData().get("type").toString());
        ZonedDateTime when = note.getData().containsKey("when")
                ? ZonedDateTime.parse(note.getData().get("when").toString() + "T00:00:00Z")
                : null;
        return Pin.builder()
                .trove(note.getTrove())
                .key(note.getId().substring(note.getId().indexOf("/")))
                .title(note.getTitle())
                .types(types)
                .text(note.getText())
                .when(when)
                .build();
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
