package w.whateva.life2.job.file.beans;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import w.whateva.life2.api.neat.NeatService;
import w.whateva.life2.api.neat.dto.ApiNeatFile;

import java.util.List;

public class NeatFileWriter implements ItemWriter<ApiNeatFile> {

    private transient Logger log = LoggerFactory.getLogger(NeatFileWriter.class);

    private final NeatService neatService;

    @Autowired
    public NeatFileWriter(NeatService neatService) {
        this.neatService = neatService;
    }

    @Override
    public void write(List<? extends ApiNeatFile> files) throws Exception {
        files.forEach(neatService::addNeatFile);
    }
}
