package w.whateva.life2.service.neat.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.support.GenericWebApplicationContext;
import w.whateva.life2.api.neat.NeatOperations;
import w.whateva.life2.api.neat.NeatService;
import w.whateva.life2.api.neat.dto.ApiNeatFile;
import w.whateva.life2.data.neat.NeatDao;
import w.whateva.life2.data.neat.domain.NeatFile;
import w.whateva.life2.data.neat.repository.NeatFileRepository;
import w.whateva.life2.data.note.repository.NoteRepository;
import w.whateva.life2.data.pin.repository.PinDao;
import w.whateva.life2.integration.api.ArtifactProvider;
import w.whateva.life2.integration.neat.NeatProvider;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NeatServiceImpl implements NeatService, NeatOperations {

    private final NeatFileRepository neatFileRepository;
    private final NeatDao neatDao;

    @Autowired
    public NeatServiceImpl(GenericWebApplicationContext context, NeatFileRepository neatFileRepository, NeatDao neatDao, NoteRepository noteRepository, PinDao pinDao) {
        this.neatFileRepository = neatFileRepository;
        this.neatDao = neatDao;
        context.registerBean("NeatProvider",
                ArtifactProvider.class,
                () -> new NeatProvider(neatFileRepository, this.neatDao, noteRepository, pinDao));
    }

    @Override
    public void addNeatFile(ApiNeatFile apiNeatFile) {

        if (null == apiNeatFile.getKey()) return;

        NeatFile neatFile = new NeatFile();
        neatFile.setId(apiNeatFile.getKey());
        BeanUtils.copyProperties(apiNeatFile, neatFile);
        neatFileRepository.save(neatFile);
    }

    @Override
    public ApiNeatFile read(String folder, String filename) {
        NeatFile neatFile = neatFileRepository.findById(composeKey(folder, filename)).orElse(null);
        if (null == neatFile) return null;
        ApiNeatFile apiNeatFile = new ApiNeatFile();
        BeanUtils.copyProperties(neatFile, apiNeatFile);
        return apiNeatFile;
    }

    @Override
    public List<ApiNeatFile> readFolder(String folder) {
        Collection<NeatFile> neatFiles = neatDao.findByFolderSorted(folder);
        if (CollectionUtils.isEmpty(neatFiles)) return Collections.emptyList();
        return neatFiles.stream().map(NeatServiceImpl::toApi).collect(Collectors.toList());
    }

    @Override
    public List<String> listFolders() {
        return neatDao.listFolders();
    }

    public List<String> allNeatFileKeys() {
        return neatFileRepository.findAll().stream().map(NeatFile::getId).collect(Collectors.toList());
    }

    public List<String> allFilenames() {
        return neatFileRepository.findAll().stream().map(NeatFile::getFilename).collect(Collectors.toList());
    }

    //@Override
    public List<ApiNeatFile> allNeatFiles() {
        return neatFileRepository.findAllByOrderByIndexAsc()
                .stream()
                .map(NeatServiceImpl::toApi)
                .collect(Collectors.toList());
    }

    private static ApiNeatFile toApi(NeatFile neatFile) {
        if (null == neatFile) return null;
        ApiNeatFile result = new ApiNeatFile();
        BeanUtils.copyProperties(neatFile, result);
        return result;
    }

    private static String composeKey(String folder, String filename) {
        return String.format("%s/%s.jpg", folder, filename).toLowerCase();
    }
}
