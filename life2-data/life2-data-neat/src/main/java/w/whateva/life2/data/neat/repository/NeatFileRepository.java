package w.whateva.life2.data.neat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import w.whateva.life2.data.neat.domain.NeatFile;

import java.util.List;

@Repository(value = "neat")
public interface NeatFileRepository extends MongoRepository<NeatFile, String> {

    List<NeatFile> findByFolderOrderByFolderAsc(String folder);

    List<NeatFile> findAllByOrderByIndexAsc();

    List<String> findDistinctByFilename();
}
