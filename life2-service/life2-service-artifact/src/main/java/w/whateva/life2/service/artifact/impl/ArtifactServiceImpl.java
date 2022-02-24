package w.whateva.life2.service.artifact.impl;

import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.GenericWebApplicationContext;
import w.whateva.life2.api.artifact.ArtifactOperations;
import w.whateva.life2.api.artifact.DataOperations;
import w.whateva.life2.api.artifact.dto.*;
import w.whateva.life2.api.person.PersonService;
import w.whateva.life2.api.person.dto.ApiPerson;
import w.whateva.life2.data.pin.PinProvider;
import w.whateva.life2.data.pin.repository.PinDao;
import w.whateva.life2.data.user.domain.User;
import w.whateva.life2.integration.api.ArtifactProvider;
import w.whateva.life2.integration.note.NoteProvider;
import w.whateva.life2.integration.note.NoteUtil;
import w.whateva.life2.service.artifact.util.ArtifactUtility;
import w.whateva.life2.service.user.UserService;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
@RestController
public class ArtifactServiceImpl implements ArtifactOperations, DataOperations {

    private Logger log = LoggerFactory.getLogger(ArtifactServiceImpl.class);

    private final GenericWebApplicationContext context;
    private final UserService userService;
    private final PersonService personService;
    private final PinProvider pinProvider;
    private final PinDao pinDao;

    @Autowired
    public ArtifactServiceImpl(GenericWebApplicationContext context, UserService userService, PersonService personService, PinDao pinDao) {
        this.context = context;
        this.userService = userService;
        this.personService = personService;
        this.pinDao = pinDao;
        this.pinProvider = new PinProvider(pinDao, personService);
    }

    @Override
    public ApiArtifact readNote(String owner, String trove, String key) {

        return providers()
                .parallelStream()
                .filter(provider -> provider instanceof NoteProvider)
                .map(p -> read(p, owner, trove, key, false))
                .filter(Objects::nonNull)
                .findAny()
                .orElse(null);
    }

    @Override
    public ApiArtifact read(String owner, String trove, String key, Boolean relatives) {

        return providers()
                .parallelStream()
                .filter(provider -> !(provider instanceof NoteProvider))
                .map(p -> read(p, owner, trove, key, relatives))
                .filter(Objects::nonNull)
                .findAny()
                .orElse(null);
    }

    private ApiArtifact read(ArtifactProvider provider, String owner, String trove, String key, Boolean relatives) {
        try {
            return provider.read(owner, trove, key, relatives);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<ApiArtifact> search(LocalDate after, LocalDate before, Set<String> who, Set<String> troves, Set<String> from, Set<String> to, String text, String source) {

        return pinProvider.search(after, before, who, troves, Collections.emptySet(), Collections.emptySet(), text, source);
    }

    @Override
    public List<ApiArtifact> search(ApiArtifactSearchSpec searchSpec) {

        ApiArtifactSearchSpec restrictedSearchSpec = restrictSearchSpec(searchSpec);

        if (null == restrictedSearchSpec) return new ArrayList<>();

        Set<String> troves = NoteUtil.parseTroves(searchSpec.getText());
        if (!CollectionUtils.isEmpty(searchSpec.getTroves())) {
            troves = Stream.concat(troves.stream(), searchSpec.getTroves().stream())
                    .collect(Collectors.toUnmodifiableSet());
        }

        restrictedSearchSpec.setWho(NoteUtil.parseWho(searchSpec.getText()));
        restrictedSearchSpec.setTroves(troves);
        restrictedSearchSpec.setText(NoteUtil.parseSearchText(searchSpec.getText()));
        restrictedSearchSpec.setSource(searchSpec.getSource());

        return pinProvider.search(
                restrictedSearchSpec.getAfter(),
                restrictedSearchSpec.getBefore(),
                restrictedSearchSpec.getWho(),
                restrictedSearchSpec.getTroves(),
                Collections.emptySet(),
                Collections.emptySet(),
                restrictedSearchSpec.getText(),
                restrictedSearchSpec.getSource());
    }

    @Override
    public List<ApiArtifactCount> count(LocalDate after, LocalDate before, Set<String> who, Set<String> troves, String text, String source) {

        return pinProvider.count(after, before, null, null, text, source);
    }

    @Override
    public List<ApiArtifactCount> count(ApiArtifactSearchSpec searchSpec) {

        ApiArtifactSearchSpec restrictedSearchSpec = restrictSearchSpec(searchSpec);

        if (null == restrictedSearchSpec) return new ArrayList<>();

        restrictedSearchSpec.setWho(NoteUtil.parseWho(searchSpec.getText()));
        restrictedSearchSpec.setTroves(NoteUtil.parseTroves(searchSpec.getText()));
        restrictedSearchSpec.setText(NoteUtil.parseSearchText(searchSpec.getText()));
        restrictedSearchSpec.setSource(searchSpec.getSource());

        return pinProvider.count(restrictedSearchSpec);
    }

    private ApiArtifactSearchSpec restrictSearchSpec(ApiArtifactSearchSpec searchSpec) {

        User currentUser = userService.getCurrentUser();
        if (null == currentUser) {
            return null;
        }

        ApiPerson person = personService.findMeAmongTheirs(currentUser.getUsername(), searchSpec.getOwner());

        if (null == person) {
            log.info("hm, couldn't find me...: " + currentUser.getUsername());
            return null;
        } else {
            log.info("found myself: " + currentUser.getUsername());
        }

        ApiArtifactSearchSpec access = new ApiArtifactSearchSpec();
        Set<String> param = Sets.newHashSet(person.getName());
        access.setTo(param);
        access.setFrom(param);
        access.setWho(param);

        return ArtifactUtility.restrict(searchSpec, access);
    }

    @Override
    public Integer index(String owner, String trove) {
        return providers()
                .parallelStream()
                .mapToInt(p -> index(p, owner, trove))
                .sum();
    }

    private int index(ArtifactProvider provider, String owner, String trove) {
        return provider.index(owner, trove);
    }

    private Collection<ArtifactProvider> providers() {

        return context.getBeansOfType(ArtifactProvider.class).values();
    }

    @Override
    public List<ApiTrove> allTroves() {
        return pinDao.listTroves().stream()
                .map(t -> {
                    ApiTrove trove = new ApiTrove();
                    trove.setName(t);
                    return trove;
                }).collect(Collectors.toUnmodifiableList());
    }

    @Override
    public List<ApiTag> allTags() {
        return pinDao.listTags().stream()
                .map(t -> {
                    ApiTag tag = new ApiTag();
                    tag.setName(t);
                    return tag;
                }).collect(Collectors.toUnmodifiableList());
    }
}
