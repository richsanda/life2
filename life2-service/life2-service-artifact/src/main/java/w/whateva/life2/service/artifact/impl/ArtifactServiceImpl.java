package w.whateva.life2.service.artifact.impl;

import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import w.whateva.life2.api.common.ArtifactOperations;
import w.whateva.life2.api.common.dto.ApiArtifact;
import w.whateva.life2.service.artifact.util.ArtifactUtility;
import w.whateva.life2.integration.email.api.ArtifactProvider;
import w.whateva.life2.service.artifact.util.bucket.AbstractLocalDateTimeOperator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Primary
@Service
public class ArtifactServiceImpl implements ArtifactOperations {

    @Autowired
    private List<ArtifactProvider> providers;

    private final ArtifactUtility artifactUtility;

    @Autowired
    public ArtifactServiceImpl(ArtifactUtility artifactUtility) {
        this.artifactUtility = artifactUtility;
    }

    @Override
    public ApiArtifact read(String key) {
        return null;
    }

    @Override
    public List<ApiArtifact> search(LocalDate after, LocalDate before, HashSet<String> names) {
        return providers
                .parallelStream()
                .map(p -> allShreds(p, after, before, names))
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<ApiArtifact> allShreds(ArtifactProvider provider, LocalDate after, LocalDate before, HashSet<String> names) {
        try {
            return provider.search(after, before, names);
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    /*
    @Override
    public List<List<ApiArtifact>> allShreds(LocalDate after, LocalDate before, HashSet<String> names, Integer numBuckets) {

        List<List<ApiArtifact>> buckets = artifactUtility.putInBuckets(
                allShreds(after, before, names),
                new AbstractLocalDateTimeOperator<ApiArtifact>() {
                    @Override
                    public LocalDateTime apply(ApiArtifact shred) {
                        return shred.getSent();
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
    */
}
