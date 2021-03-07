package w.whateva.life2.data.note;

import w.whateva.life2.data.note.domain.Note;

import java.util.List;

public interface NoteDao {

    List<String> listTroves();

    List<Note> findByTroveSorted(String folder);
}
