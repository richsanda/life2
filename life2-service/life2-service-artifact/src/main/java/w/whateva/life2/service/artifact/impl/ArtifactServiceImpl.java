package w.whateva.life2.service.artifact.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.GenericWebApplicationContext;
import w.whateva.life2.api.artifact.ArtifactOperations;
import w.whateva.life2.api.artifact.dto.ApiArtifact;
import w.whateva.life2.api.artifact.dto.ApiArtifactSearchSpec;
import w.whateva.life2.api.person.PersonService;
import w.whateva.life2.api.person.dto.ApiPerson;
import w.whateva.life2.data.user.domain.User;
import w.whateva.life2.integration.api.ArtifactProvider;
import w.whateva.life2.service.artifact.util.ArtifactUtility;
import w.whateva.life2.service.artifact.util.bucket.AbstractLocalDateTimeOperator;
import w.whateva.life2.service.user.UserService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
@Primary
@Service
public class ArtifactServiceImpl implements ArtifactOperations {

    private Logger log = LoggerFactory.getLogger(ArtifactServiceImpl.class);

    private final GenericWebApplicationContext context;
    private final ArtifactUtility artifactUtility;
    private final UserService userService;
    private final PersonService personService;

    @Autowired
    public ArtifactServiceImpl(GenericWebApplicationContext context, ArtifactUtility artifactUtility, UserService userService, PersonService personService) {
        this.context = context;
        this.artifactUtility = artifactUtility;
        this.userService = userService;
        this.personService = personService;
    }

    @Override
    public ApiArtifact read(String owner, String trove, String key) {

        return providers()
                .parallelStream()
                .map(p -> read(p, owner, trove, key))
                .filter(Objects::nonNull)
                .findAny()
                .orElse(null);
    }

    private ApiArtifact read(ArtifactProvider provider, String owner, String trove, String key) {
        try {
            return provider.read(owner, trove, key);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<ApiArtifact> search(LocalDate after, LocalDate before, HashSet<String> who, HashSet<String> from, HashSet<String> to) {

        return providers()
                .parallelStream()
                .map(p -> search(p, after, before, who, from, to))
                .flatMap(List::stream)
                .sorted(Comparator.comparing(ApiArtifact::getSent))
                .collect(Collectors.toList());
    }

    @Override
    public List<ApiArtifact> search(ApiArtifactSearchSpec searchSpec) {

        User currentUser = userService.getCurrentUser();
        if (null == currentUser) {
            return new ArrayList<>();
        }

        ApiPerson person = personService.findMeAmongTheirs(currentUser.getUsername(), searchSpec.getOwner());

        if (null == person) {
            log.info("hm, couldn't find me...: " + currentUser.getUsername());
            return new ArrayList<>();
        } else {
            log.info("found myself: " + currentUser.getUsername());
        }

        ApiArtifactSearchSpec access = new ApiArtifactSearchSpec();
        Set<String> param = Sets.newHashSet(person.getName());
        access.setTo(param);
        access.setFrom(param);
        access.setWho(param);

        ApiArtifactSearchSpec restrictedSearchSpec = ArtifactUtility.restrict(searchSpec, access);

        return providers()
                .parallelStream()
                .map(p -> search(p, restrictedSearchSpec))
                .flatMap(List::stream)
                .sorted(Comparator.comparing(ApiArtifact::getSent))
                .collect(Collectors.toList());
    }

    private List<ApiArtifact> search(ArtifactProvider provider, LocalDate after, LocalDate before, HashSet<String> who, HashSet<String> from, HashSet<String> to) {
        try {
            return provider.search(after, before, who, from, to);
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    private List<ApiArtifact> search(ArtifactProvider provider, ApiArtifactSearchSpec searchSpec) {
        try {
            return provider.search(searchSpec);
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    public List<List<ApiArtifact>> search(LocalDate after, LocalDate before, HashSet<String> who, HashSet<String> from, HashSet<String> to, Integer numBuckets) {

        List<List<ApiArtifact>> buckets = artifactUtility.putInBuckets(
                search(after, before, who, from, to),
                new AbstractLocalDateTimeOperator<ApiArtifact>() {
                    @Override
                    public LocalDateTime apply(ApiArtifact artifact) {
                        return artifact.getSent();
                    }
                },
                numBuckets,
                after.atStartOfDay(),
                before.plusDays(1).atStartOfDay()
        );
        return buckets
                .stream()
                .map(ArrayList::new)
                .collect(Collectors.toList());
    }

    private Collection<ArtifactProvider> providers() {
        return context.getBeansOfType(ArtifactProvider.class).values();
    }
}
