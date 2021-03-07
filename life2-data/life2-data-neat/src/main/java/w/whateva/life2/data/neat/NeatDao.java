package w.whateva.life2.data.neat;

import w.whateva.life2.data.neat.domain.NeatFile;

import java.util.List;

public interface NeatDao {

    List<String> listFolders();

    List<NeatFile> findByFolderSorted(String folder);
}
