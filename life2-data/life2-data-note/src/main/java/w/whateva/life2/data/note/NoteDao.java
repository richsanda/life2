package w.whateva.life2.data.note;

import w.whateva.life2.data.note.domain.Note;
import w.whateva.life2.data.note.domain.NoteMonthYearCount;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface NoteDao {

    List<String> listTroves();

    List<Note> findByTroveSorted(String folder);

    List<Note> getNotes(Set<String> who, Set<String> from, Set<String> to, LocalDateTime after, LocalDateTime before);

    List<NoteMonthYearCount> getNoteMonthYearCounts(LocalDateTime after, LocalDateTime before, Set<String> who, Set<String> troves);

    Note findByTroveAndKey(String folder, String key);
}
