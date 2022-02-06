package w.whateva.life2.data.note.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import w.whateva.life2.data.note.domain.Note;

import java.util.List;

@Repository(value = "note")
public interface NoteRepository extends MongoRepository<Note, String> {

    //List<Note> findByTroveOrderByIndexAsc(String folder);

    //List<Note> findAllByOrderByIndexAsc();

    //List<String> findDistinctByTroveOrderByAsc();

    List<Note> findAllByTrove(String trove);
}
