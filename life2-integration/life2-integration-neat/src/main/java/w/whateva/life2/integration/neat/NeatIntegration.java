package w.whateva.life2.integration.neat;

import org.springframework.stereotype.Component;
import org.springframework.web.context.support.GenericWebApplicationContext;
import w.whateva.life2.data.neat.NeatDao;
import w.whateva.life2.data.neat.repository.NeatFileRepository;
import w.whateva.life2.data.note.repository.NoteRepository;
import w.whateva.life2.data.pin.repository.PinDao;
import w.whateva.life2.integration.api.ArtifactProvider;

@Component
public class NeatIntegration {

    public NeatIntegration(GenericWebApplicationContext context, NeatFileRepository neatFileRepository, NeatDao neatDao, NoteRepository noteRepository, PinDao pinDao) {
        context.registerBean("NeatProvider",
                ArtifactProvider.class,
                () -> new NeatProvider(neatFileRepository, neatDao, noteRepository, pinDao));
    }
}
