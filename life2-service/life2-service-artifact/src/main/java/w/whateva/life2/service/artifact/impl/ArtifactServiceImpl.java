package w.whateva.life2.service.artifact.impl;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.context.support.GenericWebApplicationContext;
import w.whateva.life2.api.common.ArtifactOperations;
import w.whateva.life2.api.common.dto.ApiArtifact;
import w.whateva.life2.integration.api.ArtifactProvider;
import w.whateva.life2.service.artifact.util.ArtifactUtility;
import w.whateva.life2.service.artifact.util.bucket.AbstractLocalDateTimeOperator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 */
@Primary
@Service
// @EnableFeignClients(basePackages = "w.whateva.life2.integration")
public class ArtifactServiceImpl implements ArtifactOperations {

    private final GenericWebApplicationContext context;
    private final ArtifactUtility artifactUtility;

    @Autowired
    public ArtifactServiceImpl(GenericWebApplicationContext context, ArtifactUtility artifactUtility) {
        this.context = context;
        this.artifactUtility = artifactUtility;
    }

    @Override
    public ApiArtifact read(String key) {
        return null;
    }

    @Override
    public List<ApiArtifact> search(LocalDate after, LocalDate before, HashSet<String> names) {
        return providers()
                .parallelStream()
                .map(p -> search(p, after, before, names))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<ApiArtifact> search(ArtifactProvider provider, LocalDate after, LocalDate before, HashSet<String> names) {
        try {
            return provider.search(after, before, names);
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    public List<List<ApiArtifact>> search(LocalDate after, LocalDate before, HashSet<String> names, Integer numBuckets) {

        List<List<ApiArtifact>> buckets = artifactUtility.putInBuckets(
                search(after, before, names),
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
